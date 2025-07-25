package com.dungi.storage.rdb.repository.vote;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.VoteItem;
import com.dungi.storage.dto.vote.VoteUserDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserVoteItemJpaRepository extends CrudRepository<UserVoteItem, Long> {
    @Query("SELECT new com.dungi.storage.dto.vote.VoteUserDto(" +
            " u.profileImg," +
            " u.nickname," +
            " u.id," +
            " uvi.voteItem.choice," +
            " uvi.voteItem.id" +
            " )" +
            " FROM UserVoteItem uvi INNER JOIN User u ON uvi.userId=u.id" +
            " WHERE uvi.deleteStatus=:status AND uvi.voteItem IN :voteItemList")
    List<VoteUserDto> getVoteUser(
            @Param("voteItemList") List<VoteItem> voteItemList,
            @Param("status") DeleteStatus status
    );

    @Query("SELECT uvi " +
            " FROM UserVoteItem uvi " +
            " WHERE uvi.userId=:userId AND uvi.voteItem=:voteItem")
    Optional<UserVoteItem> findByUserAndVoteItem(
            @Param("userId") Long userId,
            @Param("voteItem") VoteItem voteItem
    );
}
