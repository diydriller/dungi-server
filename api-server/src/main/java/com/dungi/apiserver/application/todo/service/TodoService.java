package com.dungi.apiserver.application.todo.service;

import com.dungi.apiserver.application.todo.dto.CreateRepeatTodoDto;
import com.dungi.apiserver.application.todo.dto.CreateTodayTodoDto;
import com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto;
import com.dungi.common.dto.PageDto;
import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.common.value.NotificationType;
import com.dungi.core.domain.notification.query.NotificationDetail;
import com.dungi.core.domain.summary.event.UpdateWeeklyTodoCountEvent;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.core.domain.todo.util.RepeatDayUtil;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.todo.TodoStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dungi.common.util.StringUtil.NOTIFICATION_TOPIC;
import static com.dungi.common.util.StringUtil.UPDATE_WEEKLY_TODO_TOPIC;


@Service
@AllArgsConstructor
public class TodoService {
    private final TodoStore todoStore;
    private final RoomStore roomStore;
    private final MessagePublisher messagePublisher;

    // 오늘 할일 생성 기능
    // 유저가 방에 입장해있는지 확인 - 오늘 할일 생성
    @Transactional
    public Todo createTodayTodo(CreateTodayTodoDto dto, Long userId, Long roomId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var todayTodo = TodayTodo.builder()
                .todoItem(dto.getTodo())
                .roomId(roomId)
                .userId(userId)
                .deadline(TimeUtil.timeStrToLocalDateTime(dto.getTime()))
                .build();
        return todoStore.saveTodayTodo(todayTodo);
    }

    // 반복 할일 생성 기능
    // 유저가 방에 입장해있는지 검증 - 반복 할일 생성
    @Transactional
    public void createRepeatTodo(CreateRepeatTodoDto dto, Long userId, Long roomId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var repeatTodo = RepeatTodo.builder()
                .deadline(TimeUtil.timeStrToTodayLocalDateTime(dto.getTime()))
                .todoItem(dto.getTodo())
                .roomId(roomId)
                .userId(userId)
                .build();
        todoStore.saveRepeatTodo(repeatTodo, RepeatDayUtil.fromBinaryString(dto.getDays()));
    }

    // 오늘 할일 조회 기능
    // 유저가 방에 입 장해있는지 검증 - 오늘 할일 조회
    @Transactional(readOnly = true)
    public List<TodayTodo> getTodayTodo(PageDto dto) {
        roomStore.getRoomEnteredByUser(dto.getUserId(), dto.getRoomId());
        return todoStore.getTodayTodo(dto);
    }

    // 반복 할일 조회 기능
    // 유저가 방에 입장해있는지 검증 - 반복 할일 조회
    @Transactional(readOnly = true)
    public List<GetRepeatTodoDto> getRepeatTodo(PageDto dto) {
        roomStore.getRoomEnteredByUser(dto.getUserId(), dto.getRoomId());
        return todoStore.getRepeatTodo(dto).stream()
                .map(rt -> GetRepeatTodoDto.builder()
                        .todo(rt.getTodoItem())
                        .todoId(rt.getId())
                        .deadline(rt.getDeadline())
                        .day(RepeatDayUtil.toBinaryString(rt.getRepeatDayList()))
                        .userId(rt.getUserId())
                        .build()
                ).collect(Collectors.toList());
    }

    // 일을 가장 많이 한 멤버 선정 기능
    @Transactional(readOnly = true)
    public List<Long> findBestMember(Long roomId) {
        var room = roomStore.getRoom(roomId);
        var memberIdList = roomStore.getAllMemberInRoom(room).stream()
                .map(User::getId)
                .collect(Collectors.toList());

        var memberTodoCountList = todoStore.getAllMemberTodoCount(
                memberIdList,
                TimeUtil.startOfWeek(),
                TimeUtil.endOfWeek()
        );

        List<Long> bestMemberList = new ArrayList<>();
        long maxCount = 0;
        for (var memberTodoCount : memberTodoCountList) {
            maxCount = Math.max(maxCount, memberTodoCount.getTodoCount());
        }
        for (var memberTodoCount : memberTodoCountList) {
            if (memberTodoCount.getTodoCount() == maxCount) {
                bestMemberList.add(memberTodoCount.getUserId());
            }
        }
        return bestMemberList;
    }

    // 오늘 할일 완료 기능
    // 유저가 방에 입장해있는지 검증 - 마감기한이 지나지 않은 경우 오늘 할일 완료 - 주간 요일별 할일 통계 업데이트
    @Transactional
    public void completeTodayTodo(Long userId, Long roomId, Long todoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var todo = todoStore.findTodayTodo(roomId, todoId);
        todo.complete();

        messagePublisher.publish(
                UpdateWeeklyTodoCountEvent.builder()
                        .userId(userId)
                        .roomId(roomId)
                        .build(),
                UPDATE_WEEKLY_TODO_TOPIC
        );
    }

    @Transactional
    public void complimentMember(Long senderId, Long receiverId) {
        messagePublisher.publish(
                NotificationDetail.builder()
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .type(NotificationType.COMPLIMENT)
                        .build(),
                NOTIFICATION_TOPIC
        );
    }
}
