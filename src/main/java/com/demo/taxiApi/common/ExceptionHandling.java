package com.demo.taxiApi.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.model.DefaultInfo;


/**
 * @author yunsung Kim
 */
@ControllerAdvice
@RestController
public class ExceptionHandling {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    protected DefaultInfo handleError(CustomException e) {
        return DefaultInfo.builder()
            .code(e.getResultCode().name())
            .message(e.getResultCode().getMsg())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    protected DefaultInfo handleError(Exception e) {
        return DefaultInfo.builder()
            .code(AnswerCode.FAIL.name())
            .message(e.getMessage())
            .build();
    }
}
