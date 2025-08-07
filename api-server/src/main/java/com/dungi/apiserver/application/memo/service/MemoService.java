package com.dungi.apiserver.application.memo.service;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import com.dungi.apiserver.application.memo.dto.MoveMemoDto;
import com.dungi.apiserver.application.memo.dto.UpdateMemoDto;
import com.dungi.common.event.MemoCreateEvent;
import com.dungi.common.event.MemoDeleteEvent;
import com.dungi.common.event.MemoEditEvent;
import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
import com.dungi.core.integration.store.memo.MemoStore;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.message.redis.publisher.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dungi.common.util.StringUtil.*;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoStore memoStore;
    private final RoomStore roomStore;
    private final RedisPublisher publisher;

    // 메모 생성 기능
    // 유저가 방에 입장해있는지 확인 - 메모 생성
    @Transactional
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

        publisher.publish(MEMO_CREATE_CHANNEL,
                MemoCreateEvent.builder()
                        .memoId(memo.getId())
                        .memoColor(memo.getMemoColor())
                        .xPosition(memo.getXPosition())
                        .yPosition(memo.getYPosition())
                        .memoItem(memo.getMemoItem())
                        .roomId(roomId)
                        .build()
        );
    }

    // 메모 조회 기능
    // 유저가 방에 입장해있는지 검증 - 메모 조회
    @Transactional(readOnly = true)
    public List<MemoDetail> getMemo(Long roomId, Long userId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        return memoStore.getAllMemo(userId, roomId);
    }

    // 메모 수정 기능
    // 유저가 방에 입장해있는지 검증 - 메모 수정
    @Transactional
    public void updateMemo(UpdateMemoDto dto, Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        checkAuthorization(memo, userId);
        memo.updateMemo(dto.getMemo(), dto.getMemoColor());

        publisher.publish(MEMO_EDIT_CHANNEL,
                MemoEditEvent.builder()
                        .memoId(memo.getId())
                        .memoColor(dto.getMemoColor())
                        .xPosition(memo.getXPosition())
                        .yPosition(memo.getYPosition())
                        .memoItem(dto.getMemo())
                        .roomId(roomId)
                        .build()
        );
    }

    // 메모 이동 기능
    // 유저가 방에 입장해있는지 검증 - 메모 이동
    @Transactional
    public Memo moveMemo(MoveMemoDto dto, Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        memo.move(dto.getX(), dto.getY());

        publisher.publish(MEMO_EDIT_CHANNEL,
                MemoEditEvent.builder()
                        .memoId(memo.getId())
                        .memoColor(memo.getMemoColor())
                        .xPosition(dto.getX())
                        .yPosition(dto.getY())
                        .memoItem(memo.getMemoItem())
                        .roomId(roomId)
                        .build()
        );
        return memo;
    }

    // 메모 삭제 기능
    // 유저가 방에 입장해있는지 검증 - 메모 삭제
    @Transactional
    public void deleteMemo(Long roomId, Long userId, Long memoId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var memo = memoStore.getMemo(memoId);
        checkAuthorization(memo, userId);
        memo.deactivate();

        publisher.publish(MEMO_DELETE_CHANNEL,
                MemoDeleteEvent.builder()
                        .memoId(memo.getId())
                        .roomId(roomId)
                        .build()
        );
    }

    private void checkAuthorization(Memo memo, Long userId) {
        if (!memo.getUserId().equals(userId)) {
            throw new BaseException(BaseResponseStatus.AUTHORIZATION_ERROR);
        }
    }
}
