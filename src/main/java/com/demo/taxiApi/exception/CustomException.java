package com.demo.taxiApi.exception;


import lombok.Getter;

/**
 * @author yunsung Kim
 */
@Getter
public class CustomException extends RuntimeException {

    private ExceptionCode resultCode;

    public CustomException(ExceptionCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public CustomException(ExceptionCode resultCode, String additionalString) {
        super(resultCode.getMsg() + additionalString);
        this.resultCode = resultCode;
    }
}
