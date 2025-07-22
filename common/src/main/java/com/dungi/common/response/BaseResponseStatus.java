package com.dungi.common.response;

import lombok.Getter;


@Getter
public enum BaseResponseStatus {

    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    INVALID_VALUE(false, 2000, "유효하지 않는 값입니다."),

    AUTHENTICATION_ERROR(false, 2101, "인증되지 않았습니다."),
    ALREADY_EXISTS_EMAIL(false, 2102, "중복된 이메일입니다."),
    CODE_NOT_EQUAL(false, 2103, "코드가 불일치합니다."),
    CODE_NOT_EXIST(false, 2104, "코드가 존재하지않습니다."),
    AUTHORIZATION_ERROR(false, 2105, "권한이 없습니다."),
    NOT_EXISTS_EMAIL(false, 2106, "존재하지않는 이메일입니다."),
    PASSWORD_NOT_EQUAL(false, 2107, "비밀번호가 일치하지 않습니다."),
    SNS_LOGIN_FAIL(false, 2108, "SNS 로그인 실패입니다."),
    NOT_EXIST_USER(false, 2109, "존재하지않는 유저입니다"),
    NOT_USER_LOGIN(false, 2110, "로그인 되지 않은 유저입니다."),

    NOT_EXIST_ROOM(false, 2200, "존재하지않는 방입니다"),
    NOT_EXIST_USER_ROOM(false, 2201, "방에 유저가 존재하지 않습니다."),

    NOT_EXIST_VOTE(false, 2300, "존재하지않는 투표입니다."),
    NOT_EXIST_VOTEITEM(false, 2301, "존재하지않는 투표선택지입니다."),

    NOT_EXIST_MEMO(false, 2400, "존재하지않는 메모입니다."),

    NOT_EXIST_BEST_MATE(false, 2500, "일을 가장 많이 한 멤버가 없습니다."),
    NOT_EXIST_TODO(false, 2501, "존재하지 않는 할일입니다."),
    ALREADY_PASSED_DEADLINE(false, 2502, "마감기한이 지난 할일입니다."),

    NOT_EXIST_SSE(false, 2600,"존재하지않는 SSE 연결입니다."),

    SERVER_ERROR(false, 2015, "서버 에러입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;


    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
