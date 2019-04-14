package com.demo.taxiApi.service;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.demo.taxiApi.exception.ExceptionCode.NOT_FOUND_DATA;
import static com.demo.taxiApi.exception.ExceptionCode.NOT_FOUND_USER;
import static com.demo.taxiApi.exception.ExceptionCode.SIGNUP_EXIST_USERNAME;
import static com.demo.taxiApi.exception.ExceptionCode.SIGNUP_REQUIRED_EMAIL;
import static com.demo.taxiApi.exception.ExceptionCode.SIGNUP_REQUIRED_PASSWORD;
import static com.demo.taxiApi.exception.ExceptionCode.SIGNUP_REQUIRED_USERTYPE;

/**
 * @author yunsung Kim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User signin(User user) {

        if (user == null
            || StringUtils.isEmpty(user.getEmail())
            || StringUtils.isEmpty(user.getPassword())) {
            throw new CustomException(NOT_FOUND_USER);
        }

        User resultUser = repository.findByEmail(user.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (!BCrypt.checkpw(user.getPassword(), resultUser.getPassword())) {
            throw new CustomException(NOT_FOUND_USER);
        }

        return resultUser;
    }

    public User signup(User user) {

        if (user == null) {
            throw new CustomException(NOT_FOUND_DATA);
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new CustomException(SIGNUP_REQUIRED_EMAIL);
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            throw new CustomException(SIGNUP_REQUIRED_PASSWORD);
        }
        if (user.getUserType() == null) {
            throw new CustomException(SIGNUP_REQUIRED_USERTYPE);
        }
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomException(SIGNUP_EXIST_USERNAME);
        }

        User newUser = User.builder()
            .email(user.getEmail())
            .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
            .userType(user.getUserType())
            .build();

        return repository.save(newUser);
    }

    public User findByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new CustomException(NOT_FOUND_USER);
        }

        return repository.findByEmail(email)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}
