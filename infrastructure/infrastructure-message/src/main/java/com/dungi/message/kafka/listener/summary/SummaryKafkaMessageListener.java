package com.dungi.message.kafka.listener.summary;

import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.domain.summary.model.NoticeVote;
import com.dungi.core.integration.store.summary.NoticeVoteStore;
import com.dungi.core.integration.store.summary.WeeklyStatisticStore;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
}
