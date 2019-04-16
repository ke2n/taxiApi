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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void jpaTest() throws Exception {
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
        Call savedCall = null;

        for (int i=0; i < 100; i++) {
            savedCall = callRepository.save(Call.builder()
                .address("test")
                .passenger(passenger)
                .status(CallStatusCode.REQUESTED)
                .requestDate(new Date())
                .build());
        }

        savedCall.setDriver(driver);
        savedCall.setStatus(CallStatusCode.ASSIGNED);
        savedCall.setAssignDate(new Date());

        callRepository.save(savedCall);

        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Call> callList = callRepository.findAll(pageable);
        List<Call> calls = callList.getContent();

        for(Call c : calls) {
            log.info("c - {}", c);
            log.info("getEmail - {}", c.getPassenger().getEmail());
        }

        assertTrue(calls.size() > 0);
        assertEquals(CallStatusCode.ASSIGNED, calls.get(0).getStatus());
        assertEquals(CallStatusCode.REQUESTED, calls.get(1).getStatus());
    }

}
