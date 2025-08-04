package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.memo.query.MemoDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetMemoResponseDto {
    private Long memoId;
    private String profileImg;
    private String memo;
    private String memoColor;
    private boolean isOwner;
    private String createdAt;
    private double x;
    private double y;

    public static GetMemoResponseDto fromMemoDetail(MemoDetail memoDetail, Long userId) {
        return GetMemoResponseDto.builder()
                .memoId(memoDetail.getId())
                .memoColor(memoDetail.getMemoColor())
                .profileImg(memoDetail.getMemoUser().getProfileImg())
                .createdAt(TimeUtil.localDateTimeToTimeStr(memoDetail.getCreatedTime()))
                .isOwner(memoDetail.getMemoUser().getUserId().equals(userId))
                .memo(memoDetail.getMemoItem())
                .x(memoDetail.getXPosition())
                .y(memoDetail.getYPosition())
                .build();
    }
}
