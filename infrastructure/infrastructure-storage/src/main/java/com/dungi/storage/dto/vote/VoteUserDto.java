package com.dungi.storage.dto.vote;

import com.dungi.core.domain.vote.query.VoteUserDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteUserDto {
    private String profileImg;
    private String nickname;
    private Long userId;
    private String choice;
    private Long voteItemId;

    public static VoteUserDetail createVoteUserItem(VoteUserDto dto){
        return VoteUserDetail.builder()
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .profileImg(dto.getProfileImg())
                .voteUserChoice(
                        VoteUserDetail.VoteUserChoice.builder()
                                .voteItemId(dto.getVoteItemId())
                                .choice(dto.getChoice())
                                .build()
                )
                .build();
    }
}
