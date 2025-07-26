package com.dungi.apiserver.presentation.summary.dto;

import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.summary.query.NoticeVoteDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.dungi.common.util.StringUtil.NOTICE_TYPE;
import static com.dungi.common.util.StringUtil.VOTE_TYPE;

@Getter
@Builder
@AllArgsConstructor
public class GetNoticeVoteResponseDto {
    private Long id;
    private String profileImg;
    private String notice;
    private Long userId;
    private String title;
    private String isOwner;
    private String isNotice;
    private String createdAt;

    public static GetNoticeVoteResponseDto of(NoticeVoteDetail nv, Long userId) {
        return GetNoticeVoteResponseDto.builder()
                .id(nv.getId())
                .profileImg(nv.getProfileImg())
                .userId(nv.getUserId())
                .isOwner(nv.getUserId().equals(userId) ? "Y" : "N")
                .createdAt(TimeUtil.localDateTimeToTimeStr(nv.getCreatedAt()))
                .isNotice(nv.getType())
                .title(nv.getType().equals(VOTE_TYPE) ? nv.getContent() : null)
                .notice(nv.getType().equals(NOTICE_TYPE) ? nv.getContent() : null)
                .build();
    }
}
