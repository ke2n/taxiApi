package com.demo.taxiApi;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.service.AuthService;
import com.nitorcreations.junit.runners.NestedRunner;

import static com.demo.taxiApi.common.AnswerCode.NOT_FOUND_DATA;
import static com.demo.taxiApi.common.AnswerCode.UNAUTHORIZED_REQUEST;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * {@link AuthServiceTest}에 대한 단위 테스트
 *
 * @author yunsung Kim
 */
@RunWith(NestedRunner.class)
public class AuthServiceTest {

    private AuthService service;

    @Before
    public void setUp() {
        service = new AuthService();
    }

    public class createUserKey메서드_테스트 {

        @Test
        public void 파라메터가_Null또는_빈스트링일때() {
            Throwable thrown = catchThrowable(() -> service.createUserKey(null));
            assertThat(thrown).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown).getResultCode()).isEqualTo(NOT_FOUND_DATA);

            User user = User.builder().email(StringUtils.EMPTY).build();
            Throwable thrown2 = catchThrowable(() -> service.createUserKey(user));
            assertThat(thrown2).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown2).getResultCode()).isEqualTo(NOT_FOUND_DATA);
        }

        @Test
        public void 결과값이_있을때() {
            User user = User.builder().email("demo@test.com").build();
            String userKey = service.createUserKey(user);

            assertFalse(StringUtils.isEmpty(userKey));
        }
    }

    public class getNameFromToken메서드_테스트 {

        private String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoic3ZsYWRhQGdtYWlsLmNvbSIsImV4cCI6MTU1MzI3MzA1NywiZGVzYyI6Iu2FjOyKpO2KuOyaqSBEZW1vQXBp7JeQ7IScIOuwnO2WiSJ9.sJY-9_Pm8ulNIBt1Go3gHXnbxkNpCSxBlv3YdoD1OZY";
        private String normalToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI";
        private String fakelToken = "FAKE_eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE1NTM4MjcwMDYsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.nvUmBW8MeORJW3DAt6_hZ8rC8JA-GCbQa7Q5M23bljg";

        @Test
        public void 파라메터가_Null또는_빈스트링일때() {
            Throwable thrown = catchThrowable(() -> service.getEmailFromToken(null));
            assertThat(thrown).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown).getResultCode()).isEqualTo(NOT_FOUND_DATA);

            Throwable thrown2 = catchThrowable(() -> service.getEmailFromToken(StringUtils.EMPTY));
            assertThat(thrown2).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown2).getResultCode()).isEqualTo(NOT_FOUND_DATA);
        }

        @Test
        public void 만료된_토큰일_경우() {
            Throwable thrown = catchThrowable(() -> service.getEmailFromToken(expiredToken));
            assertThat(thrown).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown).getResultCode()).isEqualTo(UNAUTHORIZED_REQUEST);
        }

        @Test
        public void 변조된_토큰일_경우() {
            Throwable thrown = catchThrowable(() -> service.getEmailFromToken(fakelToken));
            assertThat(thrown).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown).getResultCode()).isEqualTo(UNAUTHORIZED_REQUEST);
        }

        @Test
        public void 만료일_내_정상_토큰일_경우() {
            String userName = service.getEmailFromToken(normalToken);
            assertFalse(StringUtils.isEmpty(userName));
        }
    }

    public class getTokenFromRequest메서드_테스트 {

        private static final String HEADER_AUTH = "Authorization";
        private static final String HEADER_INC = "Bearer ";

        @Test
        public void 파라메터가_Null일때() {
            String token = service.getTokenFromRequest(null);
            assertTrue(StringUtils.isEmpty(token));
        }

        @Test
        public void 헤더에_Authorization_속성이_없거나_Bearer_Prefix가_포함되지_않은경우() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            Throwable thrown = catchThrowable(() -> service.getTokenFromRequest(request));
            assertThat(thrown).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown).getResultCode()).isEqualTo(UNAUTHORIZED_REQUEST);

            request.addHeader(HEADER_AUTH, "just Header");

            Throwable thrown2 = catchThrowable(() -> service.getTokenFromRequest(request));
            assertThat(thrown2).isExactlyInstanceOf(CustomException.class);
            assertThat(((CustomException) thrown2).getResultCode()).isEqualTo(UNAUTHORIZED_REQUEST);
        }

        @Test
        public void 헤더에_Authorization와_Bearer_Prefix가_포함된_경우() {
            String inputToken = "token";
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(HEADER_AUTH, HEADER_INC + inputToken);
            String resultToken = service.getTokenFromRequest(request);
            assertEquals(inputToken, resultToken);
        }
    }
}
