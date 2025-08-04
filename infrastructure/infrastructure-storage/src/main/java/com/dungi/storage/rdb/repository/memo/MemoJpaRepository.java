package com.dungi.storage.rdb.repository.memo;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.storage.dto.memo.GetMemoDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoJpaRepository extends CrudRepository<Memo, Long> {
    @Query("SELECT DISTINCT new com.dungi.storage.dto.memo.GetMemoDto(" +
            " m.id," +
            " u.profileImg," +
            " m.memoItem," +
            " m.memoColor," +
            " m.createdTime," +
            " m.xPosition," +
            " m.yPosition," +
            " m.userId" +
            " )" +
            " FROM Memo m INNER JOIN User u ON m.userId=u.id " +
            " WHERE m.roomId=:roomId AND m.deleteStatus=:status")
    List<GetMemoDto> findAllMemoInRoom(
            @Param("roomId") Long roomId,
            @Param("status") DeleteStatus status
    );

    @Query("SELECT DISTINCT m" +
            " FROM Memo m" +
            " WHERE m.id=:memoId and m.deleteStatus=:status")
    Optional<Memo> findMemoById(
            @Param("memoId") Long memoId,
            @Param("status") DeleteStatus status
    );
}
