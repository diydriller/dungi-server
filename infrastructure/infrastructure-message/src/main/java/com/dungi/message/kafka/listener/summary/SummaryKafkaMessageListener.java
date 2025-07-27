package com.dungi.message.kafka.listener.summary;

import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.domain.summary.event.UpdateWeeklyTodoCountEvent;
import com.dungi.core.domain.summary.model.NoticeVote;
import com.dungi.core.domain.summary.model.WeeklyTodoCount;
import com.dungi.core.integration.store.summary.NoticeVoteStore;
import com.dungi.core.integration.store.summary.WeeklyStatisticStore;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

import static com.dungi.common.util.StringUtil.SAVE_NOTICE_VOTE_TOPIC;

@RequiredArgsConstructor
@Component
public class SummaryKafkaMessageListener {
    private final NoticeVoteStore noticeVoteStore;
    private final WeeklyStatisticStore weeklyStatisticStore;

    @KafkaListener(topics = SAVE_NOTICE_VOTE_TOPIC, groupId = "summary-group")
    public void saveNoticeVote(SaveNoticeVoteEvent event) {
        noticeVoteStore.saveNoticeVote(
                NoticeVote.builder()
                        .content(event.getContent())
                        .noticeVoteId(event.getId())
                        .createdTime(event.getCreatedTime())
                        .roomId(event.getRoomId())
                        .userId(event.getUserId())
                        .type(event.getType())
                        .build()
        );
    }

    @KafkaListener(topics = "update-weekly-todo-count", groupId = "summary-group")
    public void updateWeeklyTodoCount(UpdateWeeklyTodoCountEvent event) {
        var now = LocalDate.now();
        var weekFields = WeekFields.ISO;
        var weekOfYear = now.get(weekFields.weekOfYear());
        var dayOfWeek = now.get(weekFields.dayOfWeek());
        var year = now.getYear();

        weeklyStatisticStore.getWeeklyTodoCountByUniqueKeys(
                event.getRoomId(),
                event.getUserId(),
                year,
                weekOfYear,
                dayOfWeek
        ).ifPresentOrElse(
                weeklyTodoCount -> {
                    weeklyTodoCount.addTodoCount();
                    weeklyStatisticStore.saveWeeklyTodoCount(weeklyTodoCount);
                }, () -> {
                    var weeklyTodoCount = WeeklyTodoCount.builder()
                            .roomId(event.getRoomId())
                            .userId(event.getUserId())
                            .year(year)
                            .todoCount(1L)
                            .weekOfYear(weekOfYear)
                            .dayOfWeek(dayOfWeek)
                            .build();
                    weeklyStatisticStore.saveWeeklyTodoCount(weeklyTodoCount);
                }
        );
    }
}
