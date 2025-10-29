package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.core.domain.memo.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoResponseDto {
    private Long memoId;
    private String memoItem;
    private Double x;
    private Double y;
    private String memoColor;
    private Long roomId;
    private Long userId;

    public static MemoResponseDto fromMemo(Memo memo) {
        return MemoResponseDto.builder()
                .memoId(memo.getId())
                .memoItem(memo.getMemoItem())
                .x(memo.getXPosition())
                .y(memo.getYPosition())
                .memoColor(memo.getMemoColor())
                .roomId(memo.getRoomId())
                .userId(memo.getUserId())
                .build();
    }
}
