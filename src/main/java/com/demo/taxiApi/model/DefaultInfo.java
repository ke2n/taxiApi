package com.demo.taxiApi.model;

import com.demo.taxiApi.common.AnswerCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author yunsung Kim
 */
@JsonInclude(Include.NON_NULL)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultInfo {

    private String code;

    private String message;

    public DefaultInfo(AnswerCode code) {
        this.code = code.name();
        this.message = code.getMsg();
    }
}