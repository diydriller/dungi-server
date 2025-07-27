package com.dungi.apiserver.application.summary.dto;

import com.dungi.core.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
public class WeeklyTodoCountDto {
    private List<MemberInfo> memberInfoList;
    private Map<Integer, Map<Long, Long>> weeklyTodoCountMap;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String memberNickname;
        private String memberImageUrl;

        public static MemberInfo from(User user){
            return MemberInfo.builder()
                    .memberId(user.getId())
                    .memberNickname(user.getNickname())
                    .memberImageUrl(user.getProfileImg())
                    .build();
        }
    }
}
