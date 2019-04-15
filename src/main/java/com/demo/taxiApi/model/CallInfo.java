package com.demo.taxiApi.model;

import java.util.Date;

import com.demo.taxiApi.domain.CallStatusCode;
import com.demo.taxiApi.domain.User;
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
@AllArgsConstructor
@NoArgsConstructor
public class CallInfo {

    private Long id;

    private User passenger;

    private User driver;

    private String address;

    private CallStatusCode status;

    private Date requestDate;

    private Date assignDate;
}
