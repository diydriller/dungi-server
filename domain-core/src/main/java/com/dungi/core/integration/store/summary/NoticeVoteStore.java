package com.dungi.core.integration.store.summary;

import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.summary.query.NoticeVoteDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeVoteStore {
    List<NoticeVoteDetail> getNoticeVote(PageDto dto);

    void saveNoticeVote(com.dungi.core.domain.summary.model.NoticeVote noticeVote);
}
