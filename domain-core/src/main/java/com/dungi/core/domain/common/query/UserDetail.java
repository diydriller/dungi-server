package com.dungi.core.domain.common.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDetail {
    private Long userId;
    private String profileImg;
    private String nickname;
}
