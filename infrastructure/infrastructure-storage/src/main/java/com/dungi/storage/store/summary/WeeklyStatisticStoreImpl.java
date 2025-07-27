package com.dungi.storage.store.summary;

import com.dungi.core.domain.summary.model.WeeklyTodoCount;
import com.dungi.core.domain.summary.model.WeeklyTopUser;
import com.dungi.core.integration.store.summary.WeeklyStatisticStore;
import com.dungi.storage.rdb.repository.summary.WeeklyTodoCountJpaRepository;
import com.dungi.storage.rdb.repository.summary.WeeklyTopUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WeeklyStatisticStoreImpl implements WeeklyStatisticStore {
    private final WeeklyTodoCountJpaRepository weeklyTodoCountJpaRepository;
    private final WeeklyTopUserJpaRepository weeklyTopUserJpaRepository;

    @Override
    public Optional<WeeklyTodoCount> getWeeklyTodoCountByUniqueKeys(
            Long roomId,
            Long userId,
            Integer year,
            Integer weekOfYear,
            Integer dayOfWeek
    ) {
        return weeklyTodoCountJpaRepository.findByRoomIdAndUserIdAndYearAndWeekOfYearAndDayOfWeek(
                roomId,
                userId,
                year,
                weekOfYear,
                dayOfWeek
        );
    }

    @Override
    public void saveWeeklyTodoCount(WeeklyTodoCount weeklyTodoCount) {
        weeklyTodoCountJpaRepository.save(weeklyTodoCount);
    }

    @Override
    public List<WeeklyTodoCount> getWeeklyTodoCountListInRoom(
            Long roomId,
            Integer year,
            Integer weekOfYear,
            Integer dayOfWeek
    ) {
        return weeklyTodoCountJpaRepository.findAllByRoomIdAndYearAndWeekOfYear(
                roomId,
                year,
                weekOfYear
        );
    }

    @Override
    public List<WeeklyTodoCount> decideAndGetWeeklyTopUserInRoom(
            Long roomId,
            Integer year,
            Integer weekOfYear
    ) {
        return weeklyTodoCountJpaRepository.findAllMaxTodoCountUserByRoomIdAndYearAndWeekOfYear(
                roomId,
                year,
                weekOfYear
        );
    }

    @Override
    public List<WeeklyTopUser> getWeeklyTopUser(Long roomId, Integer year, Integer weekOfYear) {
        return weeklyTopUserJpaRepository.findAllByRoomIdAndYearAndWeekOfYear(
                roomId,
                year,
                weekOfYear
        );
    }
}
