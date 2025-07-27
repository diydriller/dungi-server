package com.dungi.apiserver.application.summary.service;

import com.dungi.apiserver.application.summary.dto.WeeklyTodoCountDto;
import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.summary.WeeklyStatisticStore;
import com.dungi.core.integration.store.user.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyStatisticService {
    private final WeeklyStatisticStore weeklyStatisticStore;
    private final RoomStore roomStore;
    private final UserStore userStore;

    @Transactional(readOnly = true)
    public WeeklyTodoCountDto getWeeklyTodoCount(Long roomId) {
        var room = roomStore.getRoom(roomId);
        var memberList = roomStore.getAllMemberInRoom(room);
        var weeklyTodoCountMap = initializeWeeklyTodoCountMap(memberList);

        var now = LocalDate.now();
        var weekFields = WeekFields.ISO;
        var year = now.getYear();
        var weekOfYear = now.get(weekFields.weekOfYear());
        var dayOfWeek = now.get(weekFields.dayOfWeek());

        var weeklyTodoCountList = weeklyStatisticStore.getWeeklyTodoCountListInRoom(
                roomId,
                year,
                weekOfYear,
                dayOfWeek
        );
        for (var weeklyTodoCount : weeklyTodoCountList) {
            weeklyTodoCountMap.get(weeklyTodoCount.getDayOfWeek() - 1)
                    .merge(weeklyTodoCount.getUserId(), 1L, Long::sum);
        }

        return WeeklyTodoCountDto.builder()
                .memberInfoList(
                        memberList.stream()
                                .map(WeeklyTodoCountDto.MemberInfo::from)
                                .collect(Collectors.toList())
                )
                .weeklyTodoCountMap(weeklyTodoCountMap)
                .build();
    }

    @Transactional
    public List<User> getWeeklyTopUser(Long roomId) {
        var lastWeekDate = LocalDate.now().minusWeeks(1);
        var weekFields = WeekFields.ISO;
        var year = lastWeekDate.getYear();
        var weekOfYear = lastWeekDate.get(weekFields.weekOfYear());

        return weeklyStatisticStore.getWeeklyTopUser(
                        roomId,
                        year,
                        weekOfYear
                ).stream()
                .map(weeklyTopUser -> userStore.getUserById(weeklyTopUser.getUserId()))
                .collect(Collectors.toList());
    }

    private Map<Integer, Map<Long, Long>> initializeWeeklyTodoCountMap(List<User> memberList) {
        var weeklyTodoCountMap = new HashMap<Integer, Map<Long, Long>>();
        for (var day : TimeUtil.DAY.values()) {
            int dayNum = day.ordinal();
            for (var member : memberList) {
                var todoCountMap = new HashMap<Long, Long>();
                todoCountMap.put(member.getId(), 0L);
                weeklyTodoCountMap.put(dayNum, todoCountMap);
            }
        }
        return weeklyTodoCountMap;
    }
}
