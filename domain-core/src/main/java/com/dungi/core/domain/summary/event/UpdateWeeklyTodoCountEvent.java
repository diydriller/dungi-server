package com.dungi.core.domain.summary.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWeeklyTodoCountEvent {
    private Long userId;

    private Long roomId;
}
