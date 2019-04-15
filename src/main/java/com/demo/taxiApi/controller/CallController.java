package com.demo.taxiApi.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.domain.UserTypeCode;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.model.CallInfo;
import com.demo.taxiApi.model.DefaultInfo;
import com.demo.taxiApi.model.RequestInfo;
import com.demo.taxiApi.service.AuthService;
import com.demo.taxiApi.service.CallService;
import com.demo.taxiApi.service.UserService;

import lombok.RequiredArgsConstructor;

import static com.demo.taxiApi.common.AnswerCode.REQUEST_ONLY_DRIVER;
import static com.demo.taxiApi.common.AnswerCode.REQUEST_ONLY_PASSENGER;
import static com.demo.taxiApi.common.AnswerCode.SUCCESS;
import static com.demo.taxiApi.common.AnswerCode.UNAUTHORIZED_REQUEST;

/**
 * @author yunsung Kim
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/call")
public class CallController {

    private final AuthService authService;

    private final CallService callService;

    private final UserService userService;

    @GetMapping("/list")
    public List<CallInfo> list(
        @PageableDefault(sort = {"id"}, direction = Direction.DESC, size = 50) Pageable pageable) {
        return callService.list(pageable);
    }

    @PostMapping("/request")
    public DefaultInfo request(@RequestBody RequestInfo requestInfo, HttpServletRequest request) {
        User user = checkAuthAndGetUser(UserTypeCode.PASSENGER, request);

        callService.makeRequest(user, requestInfo);

        return new DefaultInfo(SUCCESS);
    }

    @PostMapping("/assign")
    public DefaultInfo assign(@RequestBody RequestInfo requestInfo, HttpServletRequest request) {
        User user = checkAuthAndGetUser(UserTypeCode.DRIVER, request);

        callService.makeAssign(user, requestInfo);

        return new DefaultInfo(SUCCESS);
    }

    private User checkAuthAndGetUser(UserTypeCode userTypeCode, HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        UserTypeCode userType = authService.getUserTypeFromToken(token);

        switch (userTypeCode) {
            case PASSENGER:
                if (!userTypeCode.equals(userType)) {
                    throw new CustomException(REQUEST_ONLY_PASSENGER);
                }
                break;
            case DRIVER:
                if (!userTypeCode.equals(userType)) {
                    throw new CustomException(REQUEST_ONLY_DRIVER);
                }
                break;
            default:
                throw new CustomException(UNAUTHORIZED_REQUEST);
        }

        String email = authService.getEmailFromToken(token);
        return userService.findByEmail(email);
    }
}
