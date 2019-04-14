package com.demo.taxiApi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.taxiApi.domain.UserTypeCode;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.model.AuthInfo;
import com.demo.taxiApi.model.DefaultInfo;
import com.demo.taxiApi.service.AuthService;
import com.demo.taxiApi.service.CallService;

import static com.demo.taxiApi.exception.ExceptionCode.ONLY_DRIVER_REQUEST;
import static com.demo.taxiApi.exception.ExceptionCode.ONLY_PASSENGER_REQUEST;

/**
 * @author yunsung Kim
 */
@RestController
@RequestMapping("/api/call")
public class CallController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CallService callService;

    @GetMapping("/list")
    public AuthInfo list() {
        callService.list();
        return null;
    }

    @GetMapping("/request")
    public DefaultInfo request(HttpServletRequest request) {
        checkAuth(UserTypeCode.PASSENGER, request);

        return null;
    }

    @GetMapping("/assign")
    public DefaultInfo assign(HttpServletRequest request) {
        checkAuth(UserTypeCode.DRIVER, request);

        return null;
    }

    private void checkAuth(UserTypeCode userTypeCode, HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        UserTypeCode userType = authService.getUserTypeFromToken(token);

        switch (userTypeCode) {
            case PASSENGER:
                if (!userTypeCode.equals(userType)) {
                    throw new CustomException(ONLY_PASSENGER_REQUEST);
                }
                break;
            case DRIVER:
                if (!userTypeCode.equals(userType)) {
                    throw new CustomException(ONLY_DRIVER_REQUEST);
                }
                break;
        }
    }
}
