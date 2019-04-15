package com.demo.taxiApi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.demo.taxiApi.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * @author yunsung Kim
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String token = authService.getTokenFromRequest(req);
        String email = authService.getEmailFromToken(token);

        if (StringUtils.isEmpty(email)) {
            return false;
        }

        return true;
    }
}
