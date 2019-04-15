package com.demo.taxiApi.model;

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
public class RequestInfo {

    private Long id;

    private String address;
}
