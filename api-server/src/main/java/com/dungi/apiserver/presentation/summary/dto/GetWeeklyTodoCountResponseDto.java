package com.dungi.apiserver.presentation.summary.dto;

import com.dungi.apiserver.application.summary.dto.WeeklyTodoCountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class GetWeeklyTodoCountResponseDto {
    private List<MemberInfo> memberInfoList;
    private Map<Integer, Map<Long, Long>> weeklyTodoCountMap;

    @Getter
    @Builder
    @AllArgsConstructor
    static public class MemberInfo {
        private Long memberId;
        private String memberNickname;
        private String memberImageUrl;
    }

    public static GetWeeklyTodoCountResponseDto from(
            List<WeeklyTodoCountDto.MemberInfo> memberInfoList,
            Map<Integer, Map<Long, Long>> weeklyTodoCountMap
    ) {
        return GetWeeklyTodoCountResponseDto.builder()
                .memberInfoList(memberInfoList.stream()
                        .map(memberInfo -> MemberInfo.builder()
                                .memberId(memberInfo.getMemberId())
                                .memberNickname(memberInfo.getMemberNickname())
                                .memberImageUrl(memberInfo.getMemberImageUrl())
                                .build())
                        .collect(Collectors.toList()))
                .weeklyTodoCountMap(weeklyTodoCountMap)
                .build();
    }
}
