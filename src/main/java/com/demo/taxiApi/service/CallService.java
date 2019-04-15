package com.demo.taxiApi.service;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.demo.taxiApi.domain.Call;
import com.demo.taxiApi.domain.CallStatusCode;
import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.exception.CustomException;
import com.demo.taxiApi.mapper.CallMapper;
import com.demo.taxiApi.model.CallInfo;
import com.demo.taxiApi.model.RequestInfo;
import com.demo.taxiApi.repository.CallRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.demo.taxiApi.common.AnswerCode.ALREADY_ASSIGNED;
import static com.demo.taxiApi.common.AnswerCode.ERROR_ADDRESS;
import static com.demo.taxiApi.common.AnswerCode.ERROR_ID;
import static com.demo.taxiApi.common.AnswerCode.NOT_FOUND_CALL_ID;

/**
 * @author yunsung Kim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CallService {

    private final CallRepository repository;

    @Transactional
    public List<CallInfo> list(Pageable pageable) {
        List<Call> callPage = repository.findAllBy(pageable);

        return CallMapper.toCallInfoList(callPage);
    }

    @Transactional
    public void makeRequest(User user, RequestInfo requestInfo) {
        if (requestInfo == null || StringUtils.length(requestInfo.getAddress()) > 100) {
            throw new CustomException(ERROR_ADDRESS);
        }

        Call call = Call.builder()
            .status(CallStatusCode.REQUESTED)
            .address(requestInfo.getAddress())
            .requestDate(new Date())
            .passenger(user)
            .build();

        repository.save(call);
    }

    @Transactional
    public void makeAssign(User user, RequestInfo requestInfo) {
        if (requestInfo == null || requestInfo.getId() == null) {
            throw new CustomException(ERROR_ID);
        }

        Call call = repository.findById(requestInfo.getId())
            .orElseThrow(() -> new CustomException(NOT_FOUND_CALL_ID));

        if (CallStatusCode.ASSIGNED.equals(call.getStatus())) {
            throw new CustomException(ALREADY_ASSIGNED);
        }

        call.setStatus(CallStatusCode.ASSIGNED);
        call.setAssignDate(new Date());
        call.setDriver(user);

        repository.save(call);
    }
}
