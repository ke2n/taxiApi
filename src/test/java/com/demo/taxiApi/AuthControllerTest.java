package com.demo.taxiApi;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.demo.taxiApi.controller.AuthController;
import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.interceptor.JwtInterceptor;
import com.demo.taxiApi.interceptor.WebConfig;
import com.demo.taxiApi.service.AuthService;
import com.demo.taxiApi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.demo.taxiApi.common.AnswerCode.NOT_FOUND_DATA;
import static com.demo.taxiApi.common.AnswerCode.SIGNUP_EXIST_EMAIL;
import static com.demo.taxiApi.common.AnswerCode.UNAUTHORIZED_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yunsung Kim
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@EnableSpringDataWebSupport
public class AuthControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private static final String ROOT_URI = "/api/auth";

    private static final String HEADER_AUTH = "Authorization";
    private static final String HEADER_INC = "Bearer ";

    private final String freshToken = "FRESH_eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI";

    private final String normalToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI";

    private final String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoic3ZsYWRhQGdtYWlsLmNvbSIsImV4cCI6MTU1MzI3MzA1NywiZGVzYyI6Iu2FjOyKpO2KuOyaqSBEZW1vQXBp7JeQ7IScIOuwnO2WiSJ9.sJY-9_Pm8ulNIBt1Go3gHXnbxkNpCSxBlv3YdoD1OZY";

    private User requestUser;

    private User dbUser;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService service;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @MockBean
    private WebConfig webConfig;

    @Before
    public void setup() throws Exception {
        requestUser = User.builder()
            .email("user@test.com")
            .password("1212").build();
        dbUser = User.builder()
            .email("user@test.com")
            .password("$2a$10$jZ7qnMGdWvVRW.XP/VK21udfW5NVzfLVslaOfI5sE2VZtRksStX1y").build();
    }

    @Test
    public void 로그인__실패() throws Exception {
        given(userService.signin(any(User.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willThrow(new CustomException(NOT_FOUND_DATA));

        defaultResultActions("/signin", requestUser)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("@.code").value(NOT_FOUND_DATA.name()));
    }

    @Test
    public void 로그인__성공() throws Exception {
        given(userService.signin(any(User.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willReturn(normalToken);

        defaultResultActions("/signin", requestUser)
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.accessToken").value(normalToken));
    }

    @Test
    public void 회원가입__중복아이디_실패() throws Exception {
        given(userService.signup(any(User.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willThrow(new CustomException(SIGNUP_EXIST_EMAIL));

        defaultResultActions("/signup", requestUser)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("@.code").value(SIGNUP_EXIST_EMAIL.name()));
    }

    @Test
    public void 회원가입__성공() throws Exception {
        given(userService.signup(any(User.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willReturn(normalToken);

        defaultResultActions("/signup", requestUser)
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.accessToken").value(normalToken));
    }

    @Test
    public void 토큰갱신__헤더값없어서_실패() throws Exception {
        given(service.getTokenFromRequest(any(HttpServletRequest.class)))
            .willThrow(new CustomException(UNAUTHORIZED_REQUEST));

        defaultResultActions("/refresh", requestUser)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("@.code").value(UNAUTHORIZED_REQUEST.name()));
    }

    @Test
    public void 토큰갱신__만료_변조된_토큰으로_실패() throws Exception {
        given(service.getTokenFromRequest(any(HttpServletRequest.class))).willReturn(expiredToken);
        given(service.getEmailFromToken(any(String.class))).willThrow(new CustomException(UNAUTHORIZED_REQUEST));

        defaultResultActions("/refresh", requestUser)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("@.code").value(UNAUTHORIZED_REQUEST.name()));
    }

    @Test
    public void 토큰갱신__새로운토큰으로_갱신성공() throws Exception {
        given(service.getTokenFromRequest(any(HttpServletRequest.class))).willReturn(normalToken);
        given(service.getEmailFromToken(any(String.class))).willReturn(normalToken);
        given(userService.findByEmail(any(String.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willReturn(freshToken);

        defaultResultActions("/refresh", requestUser)
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.accessToken").value(freshToken));
    }

    @Test
    public void JwtInterceptor_테스트() throws Exception {
        given(service.getTokenFromRequest(any(HttpServletRequest.class))).willReturn(normalToken);
        given(service.getEmailFromToken(any(String.class))).willReturn(normalToken);
        given(userService.findByEmail(any(String.class))).willReturn(dbUser);
        given(service.createUserKey(any(User.class))).willReturn(freshToken);

        defaultResultActions("/refresh", requestUser)
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.accessToken").value(freshToken));
    }

    private ResultActions defaultResultActions(String url, User user) throws Exception {
        String resultJson = new ObjectMapper().writeValueAsString(user);
        RequestBuilder rb = MockMvcRequestBuilders.post(ROOT_URI + url)
            .contentType(APPLICATION_JSON_UTF8).content(resultJson);
        return mvc.perform(rb)
            .andDo(print());
    }
}
