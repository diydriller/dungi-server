## 유저
- `POST /user`
    - 회원가입
- `POST /login`
    - 로그인
- `POST /check/email`
    - 이메일 중복 확인
- `POST /phone`
    - SMS 인증번호 보내기
- `POST /check/phone`
    - SMS 인증번호 검증
- `POST /sns/user`
    - sns 회원가입
- `POST /sns/login`
    - sns 로그인
- `POST /refresh`
    - access token 재발급

## 방
- `POST /room`
    - 방 생성
- `POST /room/{roomId}/member`
    - 방 입장
- `GET /room`
    - 방 조회
- `DELETE /room/{roomId}`
    - 방 퇴장

## 메모
- `GET /room/{roomId}/memo`
    - 메모 조회
- `POST /room/{roomId}/memo`
    - 메모 생성
- `PUT /room/{roomId}/memo/update`
    - 메모 수정
- `DELETE /room/{roomId}/memo`
    - 메모 삭제
- `/app/move-memo`
    - 메모 이동

## 할일
- `POST /room/{roomId}/todo/day`
    - 오늘 할일 생성
- `GET /room/{roomId}/todo/day`
    - 오늘 할일 조회
- `POST /room/{roomId}/todo/days`
    - 반복 할일 생성
- `GET /room/{roomId}/todo/days`
    - 반복 할일 조회
- `PATCH /room/{roomId}/todo/{todoId}/day`
    - 오늘 할일 완료
- `POST /room/{roomId}/compliment`
    - 멤버 칭찬하기

## 투표
- `POST /room/{roomId}/vote`
    - 투표 생성하기
- `GET /room/{roomId}/vote/{voteId}`
    - 투표 조회하기
- `PATCH /room/{roomId}/vote/{voteId}/choice/{choiceId}`
    - 투표하기

## 공지
- `POST /room/{roomId}/notice`
    - 공지 생성하기

## 공지 & 투표
- `GET /room/{roomId}/notice-vote`
    - 공지 & 투표 조회

## 통계
- `GET /room/{roomId}/weekly-todo-count`
    - 주간 집안일 통계 조회
- `GET /room/{roomId}/weekly-todo-top-user`
    - 주간 집안일 가장 많이 한 사람 조회

## 알림
- `GET /subscribe/{memberId}`
    - 알림 조회