package com.dungi.apiserver.presentation.vote.controller;

import com.dungi.apiserver.application.vote.service.VoteService;
import com.dungi.apiserver.presentation.vote.dto.CreateVoteRequestDto;
import com.dungi.apiserver.presentation.vote.dto.GetVoteItemResponseDto;
import com.dungi.common.response.BaseResponse;
import com.dungi.core.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static com.dungi.common.response.BaseResponseStatus.SUCCESS;
import static com.dungi.common.util.StringUtil.LOGIN_USER;

@RequiredArgsConstructor
@RestController
public class VoteController {
    private final VoteService voteService;

    @PostMapping(value = "/room/{roomId}/vote")
    public BaseResponse<?> createVote(
            @PathVariable Long roomId,
            @RequestBody @Valid CreateVoteRequestDto requestDto,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        voteService.createVote(
                requestDto.createVoteDto(),
                user.getId(),
                roomId
        );
        return new BaseResponse<>(SUCCESS);
    }

    @GetMapping(value = "/room/{roomId}/vote/{voteId}")
    public BaseResponse<?> getVote(
            @PathVariable Long roomId,
            @PathVariable Long voteId,
            HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        var getVoteItemDto = voteService.getVote(roomId, user.getId(), voteId);
        var getVoteItem = GetVoteItemResponseDto.builder()
                .choice(getVoteItemDto.getChoice().stream()
                        .map(vc -> new GetVoteItemResponseDto.VoteChoiceDto(
                                vc.getChoice(),
                                vc.getVoteUser().stream()
                                        .map(vu -> new GetVoteItemResponseDto.VoteUserDto(
                                                vu.getProfileImg(),
                                                vu.getNickname()
                                        ))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()))
                .choiceIdList(getVoteItemDto.getChoiceIdList())
                .isFinished(getVoteItemDto.getIsFinished())
                .isOwner(getVoteItemDto.getIsOwner())
                .unVotedMemberCnt(getVoteItemDto.getUnVotedMemberCnt())
                .title(getVoteItemDto.getTitle())
                .build();
        return new BaseResponse<>(getVoteItem);
    }

    @PatchMapping("/room/{roomId}/vote/{voteId}/choice/{choiceId}")
    public BaseResponse<?> createVoteChoice(
            @PathVariable Long roomId,
            @PathVariable Long voteId,
            @PathVariable Long choiceId, HttpSession session
    ) {
        var user = (User) session.getAttribute(LOGIN_USER);
        voteService.createVoteChoice(roomId, user.getId(), voteId, choiceId);
        return new BaseResponse<>(SUCCESS);
    }
}
