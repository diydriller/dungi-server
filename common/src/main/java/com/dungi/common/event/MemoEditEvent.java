package com.dungi.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemoEditEvent {
    private Long roomId;
    private Long memoId;
    private String memoItem;
    private Double xPosition;
    private Double yPosition;
    private String memoColor;

    public Double getxPosition(){
        return xPosition;
    }

    public Double getyPosition(){
        return yPosition;
    }
}