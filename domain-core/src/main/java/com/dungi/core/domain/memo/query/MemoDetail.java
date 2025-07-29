package com.dungi.core.domain.memo.query;

import com.dungi.core.domain.common.query.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class MemoDetail {
    private Long id;
    private String memoItem;
    private String memoColor;
    private LocalDateTime createdTime;
    private Double xPosition;
    private Double yPosition;
    private UserDetail memoUser;
}
