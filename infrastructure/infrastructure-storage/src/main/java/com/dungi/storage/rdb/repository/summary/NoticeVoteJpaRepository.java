package com.dungi.storage.rdb.repository.summary;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.summary.query.NoticeVoteDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeVoteJpaRepository extends CrudRepository<com.dungi.core.domain.summary.model.NoticeVote, Long> {
    @Query(value = "SELECT new com.dungi.core.domain.summary.query.NoticeVoteDetail(" +
            " nv.type," +
            " nv.noticeVoteId," +
            " u.profileImg," +
            " nv.content," +
            " nv.userId," +
            " nv.createdTime" +
            " ) " +
            " FROM NoticeVote nv" +
            " INNER JOIN User u ON nv.userId=u.id" +
            " WHERE nv.roomId=:roomId AND nv.deleteStatus=:status")
    List<NoticeVoteDetail> findAllNoticeVote(
            @Param("roomId") Long roomId,
            @Param("status") DeleteStatus status,
            Pageable pageable
    );
}
