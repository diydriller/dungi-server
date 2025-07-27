package com.dungi.storage.rdb.repository.summary;

import com.dungi.core.domain.summary.model.WeeklyTodoCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyTodoCountJpaRepository extends JpaRepository<WeeklyTodoCount, Long> {
    Optional<WeeklyTodoCount> findByRoomIdAndUserIdAndYearAndWeekOfYearAndDayOfWeek(
            Long roomId,
            Long userId,
            Integer year,
            Integer weekOfYear,
            Integer dayOfWeek
    );

    List<WeeklyTodoCount> findAllByRoomIdAndYearAndWeekOfYear(
            Long roomId,
            Integer year,
            Integer weekOfYear
    );

    @Query(value = "SELECT * " +
            "FROM weekly_todo_count wtc " +
            "WHERE wtc.room_id = :roomId " +
            "AND wtc.year = :year " +
            "AND wtc.week_of_year = :weekOfYear " +
            "AND wtc.user_id IN ( " +
            "   SELECT wtc2.user_id " +
            "   FROM weekly_todo_count wtc2 " +
            "   WHERE wtc2.room_id = :roomId " +
            "   AND wtc2.year = :year " +
            "   AND wtc2.week_of_year = :weekOfYear " +
            "   GROUP BY wtc2.user_id " +
            "   HAVING SUM(wtc2.todo_count) = ( " +
            "       SELECT MAX(todo_sum) " +
            "       FROM (" +
            "           SELECT SUM(wtc4.todo_count) AS todo_sum " +
            "           FROM weekly_todo_count wtc4 " +
            "           WHERE wtc4.room_id = :roomId " +
            "           AND wtc4.year = :year " +
            "           AND wtc4.week_of_year = :weekOfYear " +
            "           GROUP BY wtc4.user_id " +
            "       ) AS todo_sum "+
            "   ) " +
            ")", nativeQuery = true
    )
    List<WeeklyTodoCount> findAllMaxTodoCountUserByRoomIdAndYearAndWeekOfYear(
            Long roomId,
            Integer year,
            Integer weekOfYear
    );
}
