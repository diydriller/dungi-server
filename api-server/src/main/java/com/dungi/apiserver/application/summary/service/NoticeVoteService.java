package com.dungi.apiserver.application.summary.service;

import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.summary.query.NoticeVoteDetail;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.summary.NoticeVoteStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeVoteService {
    private final RoomStore roomStore;
    private final NoticeVoteStore noticeVoteStore;

    @Transactional(readOnly = true)
    public List<NoticeVoteDetail> getNoticeVote(PageDto dto) {
        roomStore.getRoomEnteredByUser(dto.getUserId(), dto.getRoomId());
        return noticeVoteStore.getNoticeVote(dto);
    }
}
