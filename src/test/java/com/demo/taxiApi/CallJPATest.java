package com.demo.taxiApi;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.taxiApi.domain.Call;
import com.demo.taxiApi.domain.CallStatusCode;
import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.domain.UserTypeCode;
import com.demo.taxiApi.repository.CallRepository;
import com.demo.taxiApi.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yunsung Kim
 */
@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class CallJPATest {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws Exception {
        User passenger = User.builder()
            .email("a@test.com")
            .userType(UserTypeCode.PASSENGER)
            .build();

        User driver = User.builder()
            .email("b@test.com")
            .userType(UserTypeCode.DRIVER)
            .build();

        userRepository.save(passenger);
        userRepository.save(driver);

        //List<User> userList = userRepository.findAll();

        Call call = Call.builder()
            .address("test")
            .passenger(passenger)
            .status(CallStatusCode.ASSIGNED)
            .requestDate(new Date())
            .build();

        Call savedCall = callRepository.save(call);

        savedCall.setDriver(driver);
        savedCall.setStatus(CallStatusCode.ASSIGNED);
        savedCall.setAssignDate(new Date());

        callRepository.save(savedCall);

        callRepository.flush();

        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(0, 1, sort);
        Page<Call> callList = callRepository.findAll(pageable);
        List<Call> calls = callList.getContent();

        log.info("test - {}", calls);
        log.info("test - {}", calls.get(0).getPassenger());
        log.info("test - {}", calls.get(0).getDriver());
    }

}
