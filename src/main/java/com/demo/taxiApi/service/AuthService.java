package com.demo.taxiApi.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.domain.UserTypeCode;
import com.demo.taxiApi.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import static com.demo.taxiApi.exception.ExceptionCode.NOT_FOUND_DATA;
import static com.demo.taxiApi.exception.ExceptionCode.UNAUTHORIZED_REQUEST;


/**
 * @author yunsung Kim
 */
@Service
@Slf4j
public class AuthService {

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static final int TOKEN_EXPIRE_MINUTES = 60;
    private static final String SECRET_KEY = "test_secret_key";
    private static final String HEADER_AUTH = "Authorization";
    private static final String HEADER_INC = "Bearer ";

    public String createUserKey(User user) {
        if (user == null || StringUtils.isEmpty(user.getEmail())) {
            throw new CustomException(NOT_FOUND_DATA);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("userType", user.getUserType());
        map.put("desc", "테스트용 DemoTaxiApi에서 발행");

        return generateJWT(map);
    }

    public String getEmailFromToken(String jwt) {
        return getUserFromToken(jwt, "email");
    }

    public UserTypeCode getUserTypeFromToken(String jwt) {
        return UserTypeCode.of(getUserFromToken(jwt, "userType"));
    }

    private String getUserFromToken(String jwt, String type) {
        if (StringUtils.isEmpty(jwt)) {
            throw new CustomException(NOT_FOUND_DATA);
        }

        try {
            Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();

            log.info("expireTime :" + claims.getExpiration());
            log.info("email : {}, userType: {}, desc :{}",
                claims.get("email"), claims.get("userType"), claims.get("desc"));

            return (String) claims.get(type);
        } catch (ExpiredJwtException exception) {
            log.info("만료된 토큰");
        } catch (JwtException exception) {
            log.info("변조된 토큰");
        }
        throw new CustomException(UNAUTHORIZED_REQUEST);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return StringUtils.EMPTY;
        }

        final String authHeader = request.getHeader(HEADER_AUTH);
        if (authHeader == null || !authHeader.startsWith(HEADER_INC)) {
            throw new CustomException(UNAUTHORIZED_REQUEST);
        }

        return authHeader.substring(HEADER_INC.length());

    }

    private String generateJWT(Map<String, Object> claimsMap) {
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * TOKEN_EXPIRE_MINUTES);

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
            .setClaims(claimsMap)
            .setExpiration(expireTime)
            .signWith(signatureAlgorithm, new SecretKeySpec(
                DatatypeConverter.parseBase64Binary(SECRET_KEY), signatureAlgorithm.getJcaName()));

        return builder.compact();
    }
}
