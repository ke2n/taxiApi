package com.demo.taxiApi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.model.AuthInfo;
import com.demo.taxiApi.service.AuthService;
import com.demo.taxiApi.service.UserService;

/**
 * @author yunsung Kim
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public AuthInfo signin(@RequestBody User user) {
        User resultUser = userService.signin(user);
        String accessToken = authService.createUserKey(resultUser);

        return AuthInfo.builder()
            .accessToken(accessToken)
            .build();
    }

    @PostMapping("/signup")
    public AuthInfo signup(@RequestBody User user) {
        User resultUser = userService.signup(user);
        String accessToken = authService.createUserKey(resultUser);

        return AuthInfo.builder()
            .accessToken(accessToken)
            .build();
    }

    @PostMapping("/refresh")
    public AuthInfo refresh(HttpServletRequest request) {
        String token = authService.getTokenFromRequest(request);
        String email = authService.getEmailFromToken(token);

        User resultUser = userService.findByEmail(email);
        String accessToken = authService.createUserKey(resultUser);

        return AuthInfo.builder()
            .accessToken(accessToken)
            .build();
    }
}
