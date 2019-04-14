package com.demo.taxiApi.exception;

import lombok.Getter;

/**
 * @author yunsung Kim
 */
@Getter
public enum ExceptionCode {
    FAIL("요청에 실패하였습니다."),

    UNAUTHORIZED_REQUEST("허가되지않은 요청입니다."),
    ONLY_DRIVER_REQUEST("기사만 요청할 수 있는 기능 입니다."),
    ONLY_PASSENGER_REQUEST("승객만 요청할 수 있는 기능 입니다."),

    NOT_FOUND_SUPPORT_ID("존재 하지 않는 ID 입니다."),
    NOT_FOUND_DATA("결과값이 없습니다."),

    NOT_FOUND_USER("유저정보가 존재하지 않습니다."),
    NOT_FOUND_USERTYPE("유저타입이 올바르지 않습니다."),
    SIGNUP_REQUIRED_EMAIL("등록할 이메일을 입력해 주세요."),
    SIGNUP_REQUIRED_PASSWORD("등록할 패스워드를 입력해 주세요."),
    SIGNUP_REQUIRED_USERTYPE("유저의 타입(승객/기사)을 입력해 주세요."),
    SIGNUP_EXIST_USERNAME("이미 등록된 유저이름 입니다."),
    ;

    private String msg;

    ExceptionCode(String msg) {
        this.msg = msg;
    }

}
