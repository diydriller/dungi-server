package com.dungi.storage.store.memo;

import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
import com.dungi.core.integration.store.memo.MemoStore;
import com.dungi.storage.dto.memo.GetMemoDto;
import com.dungi.storage.rdb.repository.memo.MemoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemoStoreImpl implements MemoStore {
    private final MemoJpaRepository memoJpaRepository;

    @Override
    public void saveMemo(Memo memo) {
        memoJpaRepository.save(memo);
    }

    @Override
    public List<MemoDetail> getAllMemo(Long userId, Long roomId) {
        return memoJpaRepository.findAllMemoInRoom(roomId, DeleteStatus.NOT_DELETED).stream()
                .map(GetMemoDto::createMemoInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Memo getMemo(Long memoId) {
        return memoJpaRepository.findMemoById(memoId, DeleteStatus.NOT_DELETED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_MEMO));
    }
}
