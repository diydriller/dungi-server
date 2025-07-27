package com.dungi.storage.store.todo;

import com.dungi.common.dto.PageDto;
import com.dungi.common.exception.NotFoundException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.common.value.FinishStatus;
import com.dungi.core.domain.todo.model.RepeatDay;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.core.domain.todo.query.TodoStatistic;
import com.dungi.core.integration.store.todo.TodoStore;
import com.dungi.storage.dto.todo.GetTodoCountDto;
import com.dungi.storage.rdb.repository.todo.RepeatDayJdbcRepository;
import com.dungi.storage.rdb.repository.todo.TodoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TodoStoreImpl implements TodoStore {
    private final TodoJpaRepository todoJpaRepository;
    private final RepeatDayJdbcRepository repeatDayJdbcRepository;

    @Override
    public Todo saveTodayTodo(TodayTodo todayTodo) {
        return todoJpaRepository.save(todayTodo);
    }

    @Override
    public void saveRepeatTodo(RepeatTodo repeatTodo, List<RepeatDay> repeatDayList) {
        var savedRepeatTodo = todoJpaRepository.save(repeatTodo);
        repeatDayList.forEach(rd -> rd.setRepeatTodo(savedRepeatTodo));
        repeatDayJdbcRepository.saveAll(repeatDayList);
    }

    @Override
    public List<TodayTodo> getTodayTodo(PageDto dto) {

        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.DESC, "createdTime");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime todayLastTime = LocalDate.now().plusDays(1).atStartOfDay();
        return todoJpaRepository.findAllPossibleTodayTodo(
                dto.getRoomId(),
                DeleteStatus.NOT_DELETED,
                FinishStatus.UNFINISHED,
                currentTime,
                todayLastTime,
                pageRequest
        );
    }

    @Override
    public List<RepeatTodo> getRepeatTodo(PageDto dto) {

        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.DESC, "createdTime");
        return todoJpaRepository.findAllPossibleRepeatTodo(
                dto.getRoomId(),
                DeleteStatus.NOT_DELETED,
                pageRequest
        );
    }

    @Override
    public List<TodoStatistic> getAllMemberTodoCount(
            List<Long> userIdList,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return todoJpaRepository.finAllMemberTodoCount(
                        userIdList,
                        TimeUtil.startOfWeek(),
                        TimeUtil.endOfWeek(),
                        DeleteStatus.NOT_DELETED,
                        FinishStatus.FINISHED
                ).stream()
                .map(GetTodoCountDto::createTodoStatisticInfo)
                .collect(Collectors.toList());
    }

    @Override
    public TodayTodo findTodayTodo(Long roomId, Long todoId) {
        return todoJpaRepository.findByDeleteStatusAndRoomIdAndId(DeleteStatus.NOT_DELETED, roomId, todoId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.NOT_EXIST_TODO));
    }
}
