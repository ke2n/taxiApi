package com.demo.taxiApi.domain;

import java.util.Arrays;

import com.demo.taxiApi.exception.CustomException;

import static com.demo.taxiApi.common.AnswerCode.NOT_FOUND_USERTYPE;

/**
 * @author yunsung Kim
 */
public enum UserTypeCode {
    DRIVER,
    PASSENGER;

    public static UserTypeCode of(String str) {
        return Arrays.stream(UserTypeCode.values())
            .filter(e -> e.name().equals(str.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new CustomException(NOT_FOUND_USERTYPE));
    }
}
