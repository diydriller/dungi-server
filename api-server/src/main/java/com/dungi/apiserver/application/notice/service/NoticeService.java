package com.dungi.apiserver.application.notice.service;

import com.dungi.apiserver.application.notice.dto.CreateNoticeDto;
import com.dungi.core.domain.notice.model.Notice;
import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.notice.NoticeStore;
import com.dungi.core.integration.store.room.RoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dungi.common.util.StringUtil.NOTICE_TYPE;


@Service
@RequiredArgsConstructor
public class NoticeService {
    private final RoomStore roomStore;
    private final NoticeStore noticeStore;
    private final MessagePublisher messagePublisher;

    // 공지 생성 기능
    // 방에 유저 있는지 조회 - 공지 생성 - 조회용 테이블 데이터 생성
    @Transactional
    public void createNotice(CreateNoticeDto dto) {
        roomStore.getRoomEnteredByUser(dto.getUserId(), dto.getRoomId());

        var notice = Notice.builder()
                .noticeItem(dto.getNoticeItem())
                .roomId(dto.getRoomId())
                .userId(dto.getUserId())
                .build();
        var savedNotice = noticeStore.saveNotice(notice);

        messagePublisher.publish(
                SaveNoticeVoteEvent.builder()
                        .id(savedNotice.getId())
                        .type(NOTICE_TYPE)
                        .content(dto.getNoticeItem())
                        .createdTime(savedNotice.getCreatedTime())
                        .roomId(dto.getRoomId())
                        .userId(dto.getUserId())
                        .build(),
                "save-notice-vote"
        );
    }
}
