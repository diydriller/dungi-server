package com.dungi.storage.store.summary;

import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.summary.query.NoticeVoteDetail;
import com.dungi.core.integration.store.summary.NoticeVoteStore;
import com.dungi.storage.rdb.repository.summary.NoticeVoteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class NoticeVoteStoreImpl implements NoticeVoteStore {
    private final NoticeVoteJpaRepository noticeVoteJpaRepository;

    public List<NoticeVoteDetail> getNoticeVote(PageDto dto) {

        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.DESC, "createdTime");
        return noticeVoteJpaRepository.findAllNoticeVote(
                dto.getRoomId(),
                DeleteStatus.NOT_DELETED,
                pageRequest
        );
    }

    public void saveNoticeVote(com.dungi.core.domain.summary.model.NoticeVote noticeVote) {
        noticeVoteJpaRepository.save(noticeVote);
    }
}