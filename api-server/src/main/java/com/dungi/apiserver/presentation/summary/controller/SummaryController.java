package com.dungi.apiserver.presentation.summary.controller;

import com.dungi.apiserver.application.summary.service.NoticeVoteService;
import com.dungi.apiserver.application.summary.service.WeeklyStatisticService;
import com.dungi.apiserver.presentation.summary.dto.GetNoticeVoteResponseDto;
import com.dungi.apiserver.presentation.summary.dto.GetWeeklyTodoCountResponseDto;
import com.dungi.apiserver.presentation.summary.dto.GetWeeklyTopUserResponseDto;
import com.dungi.common.dto.PageDto;
import com.dungi.common.response.BaseResponse;
import com.dungi.core.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.stream.Collectors;

import static com.dungi.common.util.StringUtil.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class SummaryController {
    private final NoticeVoteService noticeVoteService;
    private final WeeklyStatisticService weeklyStatisticService;

    @GetMapping(value = "/room/{roomId}/noticeVote")
    public BaseResponse<?> getNoticeVote(
            @PathVariable Long roomId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);

        var noticeVoteList = noticeVoteService.getNoticeVote(
                        PageDto.builder()
                                .roomId(roomId)
                                .userId(user.getId())
                                .page(page)
                                .size(size)
                                .build()
                ).stream()
                .map(nv -> GetNoticeVoteResponseDto.of(nv, user.getId())
                ).collect(Collectors.toList());
        return new BaseResponse<>(noticeVoteList);
    }

    @GetMapping(value = "/room/{roomId}/weekly-todo-count")
    public BaseResponse<?> getWeeklyTodoCount(
            @PathVariable Long roomId
    ) {
        var weeklyTodoCountDto = weeklyStatisticService.getWeeklyTodoCount(roomId);
        return new BaseResponse<>(
                GetWeeklyTodoCountResponseDto.from(
                        weeklyTodoCountDto.getMemberInfoList(),
                        weeklyTodoCountDto.getWeeklyTodoCountMap()
                )
        );
    }

    @GetMapping(value = "/room/{roomId}/weekly-todo-user")
    public BaseResponse<?> getWeeklyTopUser(
            @PathVariable Long roomId
    ) {
        var userList = weeklyStatisticService.getWeeklyTopUser(roomId);
        return new BaseResponse<>(
                userList.stream().map(
                        user -> GetWeeklyTopUserResponseDto.builder()
                                .memberId(user.getId())
                                .memberNickname(user.getNickname())
                                .memberImageUrl(user.getProfileImg())
                                .build()
                )
        );
    }
}