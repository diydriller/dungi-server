package com.dungi.core.integration.store.todo;

import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.todo.model.RepeatDay;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.core.domain.todo.query.TodoStatistic;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoStore {
    Todo saveTodayTodo(TodayTodo todo);

    void saveRepeatTodo(RepeatTodo todo, List<RepeatDay> repeatDayList);

    List<TodayTodo> getTodayTodo(PageDto dto);

    List<RepeatTodo> getRepeatTodo(PageDto dto);

    List<TodoStatistic> getAllMemberTodoCount(
            List<Long> userIdList,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    TodayTodo findTodayTodo(Long roomId, Long todoId);
}
