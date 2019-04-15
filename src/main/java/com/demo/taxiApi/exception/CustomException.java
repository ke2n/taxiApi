package com.demo.taxiApi.exception;


import com.demo.taxiApi.common.AnswerCode;

import lombok.Getter;

/**
 * @author yunsung Kim
 */
@Getter
public class CustomException extends RuntimeException {

    private AnswerCode resultCode;

    public CustomException(AnswerCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public CustomException(AnswerCode resultCode, String additionalString) {
        super(resultCode.getMsg() + additionalString);
        this.resultCode = resultCode;
    }
}
