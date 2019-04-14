package com.demo.taxiApi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

/**
 * @author yunsung Kim
 */
@JsonInclude(Include.NON_NULL)
@Builder
@Getter
public class DefaultInfo {

    String code;

    String message;
}