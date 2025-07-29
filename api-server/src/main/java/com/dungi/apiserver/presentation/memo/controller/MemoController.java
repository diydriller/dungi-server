package com.dungi.apiserver.presentation.memo.controller;

import com.dungi.apiserver.application.memo.service.MemoService;
import com.dungi.apiserver.presentation.memo.dto.*;
import com.dungi.common.response.BaseResponse;
import com.dungi.core.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.dungi.common.response.BaseResponseStatus.SUCCESS;
import static com.dungi.common.util.StringUtil.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/room/{roomId}/memo")
    BaseResponse<?> createMemo(
            @PathVariable Long roomId,
            @RequestBody @Valid CreateMemoRequestDto memoRequestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        memoService.createMemo(
                memoRequestDto.createMemoDto(),
                roomId,
                user.getId()
        );
        return new BaseResponse<>(SUCCESS);
    }

    @GetMapping("/room/{roomId}/memo")
    BaseResponse<?> getMemo(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        var memoList = memoService.getMemo(roomId, user.getId()).stream()
                .map(memoDetail -> GetMemoResponseDto.fromMemoDetail(memoDetail, user.getId()));
        return new BaseResponse<>(memoList);
    }

    @PutMapping("/room/{roomId}/memo/{memoId}/update")
    BaseResponse<?> updateMemo(
            @PathVariable Long roomId,
            @PathVariable Long memoId,
            @RequestBody @Valid UpdateMemoRequestDto memoRequestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        memoService.updateMemo(
                memoRequestDto.createUpdateMemoDto(),
                roomId,
                user.getId(),
                memoId
        );
        return new BaseResponse<>(SUCCESS);
    }

    @MessageMapping("/move-memo")
    void moveMemo(
            @Valid MoveMemoRequestDto memoRequestDto
    ) {
        var movedMemo = memoService.moveMemo(
                memoRequestDto.createMemoDto(),
                memoRequestDto.getRoomId(),
                memoRequestDto.getUserId(),
                memoRequestDto.getMemoId()
        );
        var response = MoveMemoResponseDto.fromEntity(movedMemo);
        messagingTemplate.convertAndSend("/topic/room/$roomId/move-memo", response);
    }

    @DeleteMapping("/room/{roomId}/memo/{memoId}")
    BaseResponse<?> deleteMemo(
            @PathVariable Long roomId,
            @PathVariable Long memoId, HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        memoService.deleteMemo(roomId, user.getId(), memoId);
        return new BaseResponse<>(SUCCESS);
    }
}
