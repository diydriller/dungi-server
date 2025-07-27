package com.dungi.apiserver.presentation.todo.controller;

import com.dungi.apiserver.application.todo.service.TodoService;
import com.dungi.apiserver.presentation.todo.dto.*;
import com.dungi.common.dto.PageDto;
import com.dungi.common.response.BaseResponse;
import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static com.dungi.common.response.BaseResponseStatus.SUCCESS;
import static com.dungi.common.util.StringUtil.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/room/{roomId}/todo/day")
    BaseResponse<?> createTodayTodo(
            @PathVariable Long roomId,
            @RequestBody @Valid CreateTodayTodoRequestDto todoRequestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        var todo = todoService.createTodayTodo(
                todoRequestDto.createTodayTodoDto(),
                user.getId(),
                roomId
        );
        return new BaseResponse<>(
                CreateTodayTodoResponseDto.builder()
                        .id(todo.getId())
                        .todoItem(todo.getTodoItem())
                        .build()
        );
    }

    @PostMapping("/room/{roomId}/todo/days")
    BaseResponse<?> createRepeatTodo(
            @PathVariable Long roomId,
            @RequestBody @Valid CreateRepeatTodoRequestDto requestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        todoService.createRepeatTodo(
                requestDto.createRepeatTodoDto(),
                user.getId(),
                roomId
        );
        return new BaseResponse<>(SUCCESS);
    }

    @GetMapping("/room/{roomId}/todo/day")
    BaseResponse<?> getTodayTodo(
            @PathVariable Long roomId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        var todayTodo = todoService.getTodayTodo(
                        PageDto.builder()
                                .userId(user.getId())
                                .roomId(roomId)
                                .page(page)
                                .size(size)
                                .build()
                ).stream()
                .map(t -> GetTodayTodoResponseDto.builder()
                        .todo(t.getTodoItem())
                        .todoId(t.getId())
                        .isOwner(t.getUserId().equals(user.getId()))
                        .deadline(TimeUtil.localDateTimeToTimeStr(t.getDeadline()))
                        .build())
                .collect(Collectors.toList());
        return new BaseResponse<>(todayTodo);
    }

    @GetMapping("/room/{roomId}/todo/days")
    BaseResponse<?> getRepeatTodo(
            @PathVariable Long roomId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);

        var repeatTodoList = todoService.getRepeatTodo(
                        PageDto.builder()
                                .roomId(roomId)
                                .userId(user.getId())
                                .page(page)
                                .size(size)
                                .build()
                ).stream()
                .map(t -> GetRepeatTodoResponseDto.builder()
                        .todoId(t.getTodoId())
                        .todo(t.getTodo())
                        .deadline(t.getDeadline().getHour() + "/" + t.getDeadline().getMinute())
                        .isOwner(t.getUserId().equals(user.getId()))
                        .day(t.getDay())
                        .build()
                ).collect(Collectors.toList());
        return new BaseResponse<>(repeatTodoList);
    }

    @PatchMapping("/room/{roomId}/todo/{todoId}/day")
    BaseResponse<?> completeTodayTodo(
            @PathVariable Long roomId,
            @PathVariable Long todoId,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);

        todoService.completeTodayTodo(user.getId(), roomId, todoId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/room/{roomId}/compliment")
    BaseResponse<?> complimentMember(
            @PathVariable Long roomId,
            @RequestParam Long memberId,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);

        todoService.complimentMember(user.getId(), memberId);
        return new BaseResponse<>(SUCCESS);
    }
}
