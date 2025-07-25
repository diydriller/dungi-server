package com.dungi.apiserver.presentation.notice.controller;

import com.dungi.apiserver.application.notice.service.NoticeService;
import com.dungi.apiserver.presentation.notice.dto.CreateNoticeRequestDto;
import com.dungi.common.response.BaseResponse;
import com.dungi.core.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.dungi.common.response.BaseResponseStatus.SUCCESS;
import static com.dungi.common.util.StringUtil.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(value = "/room/{roomId}/notice")
    public BaseResponse<?> createNotice(
            @PathVariable Long roomId,
            @RequestBody @Valid CreateNoticeRequestDto noticeRequestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        noticeService.createNotice(
                noticeRequestDto.createNoticeDto(roomId, user.getId())
        );
        return new BaseResponse<>(SUCCESS);
    }
}
