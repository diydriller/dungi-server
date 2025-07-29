package com.dungi.storage.dto.memo;

import com.dungi.core.domain.common.query.UserDetail;
import com.dungi.core.domain.memo.query.MemoDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMemoDto {
    private Long id;
    private String profileImg;
    private String memoItem;
    private String memoColor;
    private LocalDateTime createdTime;
    private Double xPosition;
    private Double yPosition;
    private Long userId;

    public static MemoDetail createMemoInfo(GetMemoDto dto){
        return MemoDetail.builder()
                .id(dto.getId())
                .memoColor(dto.getMemoColor())
                .memoItem(dto.getMemoItem())
                .createdTime(dto.getCreatedTime())
                .xPosition(dto.getXPosition())
                .yPosition(dto.getYPosition())
                .memoUser(UserDetail.builder()
                        .userId(dto.getUserId())
                        .profileImg(dto.getProfileImg())
                        .build())
                .build();
    }
}
