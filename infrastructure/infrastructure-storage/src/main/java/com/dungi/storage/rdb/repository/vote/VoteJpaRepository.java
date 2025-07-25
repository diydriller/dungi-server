package com.dungi.storage.rdb.repository.vote;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.vote.model.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteJpaRepository extends CrudRepository<Vote, Long> {
    @Query("SELECT v " +
            " FROM Vote v" +
            " WHERE v.id=:voteId AND v.deleteStatus=:status")
    Optional<Vote> findByIdAndDeleteStatus(
            @Param("voteId") Long voteId,
            @Param("status") DeleteStatus status
    );
}
