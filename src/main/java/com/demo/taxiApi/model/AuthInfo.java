package com.demo.taxiApi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

/**
 * @author yunsung Kim
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class AuthInfo {

    private String accessToken;
}
