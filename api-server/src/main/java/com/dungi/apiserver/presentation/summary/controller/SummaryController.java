package com.dungi.apiserver.presentation.summary.controller;

import com.dungi.apiserver.application.summary.service.NoticeVoteService;
import com.dungi.apiserver.application.summary.service.WeeklyStatisticService;
import com.dungi.apiserver.presentation.summary.dto.GetNoticeVoteResponseDto;
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
}