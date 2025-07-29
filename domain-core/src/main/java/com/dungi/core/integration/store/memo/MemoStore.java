package com.dungi.core.integration.store.memo;

import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoStore {
    void saveMemo(Memo memo);

    List<MemoDetail> getAllMemo(Long userId, Long roomId);

    Memo getMemo(Long memoId);
}
