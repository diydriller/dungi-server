package com.dungi.storage.rdb.repository.todo;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.common.value.FinishStatus;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.storage.dto.todo.GetTodoCountDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoJpaRepository extends CrudRepository<Todo, Long> {
    Optional<Todo> findById(Long id);

    @Query(value = "SELECT tt " +
            " FROM TodayTodo tt" +
            " WHERE tt.roomId=:roomId " +
            " AND tt.deleteStatus=:deleteStatus " +
            " AND tt.finishStatus=:finishStatus " +
            " AND tt.deadline BETWEEN :currentTime AND :todayLastTime",
            countQuery = "SELECT COUNT(tt.id) FROM TodayTodo tt")
    List<TodayTodo> findAllPossibleTodayTodo(
            @Param("roomId") Long roomId,
            @Param("deleteStatus") DeleteStatus deleteStatus,
            @Param("finishStatus") FinishStatus finishStatus,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("todayLastTime") LocalDateTime todayLastTime,
            Pageable pageable
    );


    @Query(value = "SELECT rt " +
            " FROM RepeatTodo rt" +
            " WHERE rt.roomId=:roomId AND rt.deleteStatus=:status",
            countQuery = "SELECT COUNT(rt.id) FROM RepeatTodo rt")
    List<RepeatTodo> findAllPossibleRepeatTodo(
            @Param("roomId") Long roomId,
            @Param("status") DeleteStatus status,
            Pageable pageable
    );

    @Query(value = "SELECT new com.dungi.storage.dto.todo.GetTodoCountDto(" +
            " u.id," +
            " COUNT(t.id)" +
            " ) " +
            " FROM TodayTodo t " +
            " INNER JOIN User u ON t.userId=u.id" +
            " WHERE t.finishStatus=:finishStatus " +
            " AND t.deleteStatus=:deleteStatus " +
            " AND t.deadline BETWEEN :startDate AND :endDate" +
            " AND u.id IN :userIdList" +
            " GROUP BY u.id")
    List<GetTodoCountDto> finAllMemberTodoCount(
            @Param("userIdList") List<Long> userIdList,
            @Param("startDate") LocalDateTime start_date,
            @Param("endDate") LocalDateTime end_date,
            @Param("deleteStatus") DeleteStatus deleteStatus,
            @Param("finishStatus") FinishStatus finishStatus
    );

    @Query("SELECT tt FROM TodayTodo tt " +
            "WHERE tt.id = :todoId AND tt.roomId = :roomId AND tt.deleteStatus = :deleteStatus")
    Optional<TodayTodo> findByDeleteStatusAndRoomIdAndId(DeleteStatus deleteStatus, Long roomId, Long todoId);
}
