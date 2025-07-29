package com.dungi.apiserver.application.memo.service;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import com.dungi.apiserver.application.memo.dto.MoveMemoDto;
import com.dungi.apiserver.application.memo.dto.UpdateMemoDto;
import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
import com.dungi.core.integration.store.memo.MemoStore;
import com.dungi.core.integration.store.room.RoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoStore memoStore;
    private final RoomStore roomStore;
    private final SimpMessagingTemplate messagingTemplate;

    // 메모 생성 기능
    // 유저가 방에 입장해있는지 확인 - 메모 생성
    // 메모 생성시 캐시 삭제
    @Transactional
    @CacheEvict(key = "#roomId", value = "getMemo")
    public void createMemo(CreateMemoDto dto, Long roomId, Long userId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = Memo.builder()
                .userId(userId)
                .roomId(roomId)
                .memoItem(dto.getMemoItem())
                .xPosition(dto.getXPosition())
                .yPosition(dto.getYPosition())
                .memoColor(dto.getMemoColor())
                .build();
        memoStore.saveMemo(memo);
    }

    // 메모 조회 기능
    // 유저가 방에 입장해있는지 검증 - 메모 조회
    // 캐시 사용해서 조회
    @Transactional(readOnly = true)
    @Cacheable(key = "#roomId", value = "getMemo")
    public List<MemoDetail> getMemo(Long roomId, Long userId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        return memoStore.getAllMemo(userId, roomId);
    }

    // 메모 수정 기능
    // 유저가 방에 입장해있는지 검증 - 메모 수정
    // 메모 수정시 캐시 삭제
    @Transactional
    @CacheEvict(key = "#roomId", value = "getMemo")
    public void updateMemo(UpdateMemoDto dto, Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        if (memo.getId().equals(memoId)) {
            throw new BaseException(BaseResponseStatus.AUTHORIZATION_ERROR);
        }
        memo.updateMemo(dto.getMemo(), dto.getMemoColor());
    }

    // 메모 이동 기능
    // 유저가 방에 입장해있는지 검증 - 메모 이동
    // 메모 이동시 캐시 삭제
    @Transactional
    @CacheEvict(key = "#roomId", value = "getMemo")
    public Memo moveMemo(MoveMemoDto dto, Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        memo.move(dto.getX(), dto.getY());
        return memo;
    }

    // 메모 삭제 기능
    // 유저가 방에 입장해있는지 검증 - 메모 삭제
    // 메모 삭제시 캐시 삭제
    @Transactional
    @CacheEvict(key = "#roomId", value = "getMemo")
    public void deleteMemo(Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        if (memo.getId().equals(memoId)) {
            throw new BaseException(BaseResponseStatus.AUTHORIZATION_ERROR);
        }
        memo.deactivate();
    }
}
