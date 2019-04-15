package com.demo.taxiApi.common;

import lombok.Getter;

/**
 * @author yunsung Kim
 */
@Getter
public enum AnswerCode {
    FAIL("요청에 실패하였습니다."),
    SUCCESS("요청이 정상적으로 완료 되었습니다."),

    UNAUTHORIZED_REQUEST("허가되지않은 요청입니다."),
    REQUEST_ONLY_DRIVER("기사만 요청할 수 있는 기능 입니다."),
    REQUEST_ONLY_PASSENGER("승객만 요청할 수 있는 기능 입니다."),

    NOT_FOUND_CALL_ID("존재 하지 않는 ID 입니다."),
    NOT_FOUND_DATA("결과값이 없습니다."),
    NOT_FOUND_USER("유저정보가 존재하지 않습니다."),
    NOT_FOUND_USERTYPE("유저타입이 올바르지 않습니다."),
    INVALID_EMAIL_FORMAT("잘못된 이메일 형식입니다."),
    SIGNUP_REQUIRED_EMAIL("등록할 이메일을 입력해 주세요."),
    SIGNUP_REQUIRED_PASSWORD("등록할 패스워드를 입력해 주세요."),
    SIGNUP_REQUIRED_USERTYPE("유저의 타입(승객/기사)을 입력해 주세요."),
    SIGNUP_EXIST_USERNAME("이미 등록된 유저이름 입니다."),

    ALREADY_ASSIGNED("이미 할당된 배차입니다."),

    ERROR_ADDRESS("주소값이 없거나 100자를 넘었습니다."),
    ERROR_ID("ID값이 없습니다."),
    ;

    private String msg;

    AnswerCode(String msg) {
        this.msg = msg;
    }

}
