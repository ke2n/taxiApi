package com.demo.taxiApi.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.demo.taxiApi.domain.Call;
import com.demo.taxiApi.model.CallInfo;

import static java.util.stream.Collectors.toList;

/**
 * @author yunsung Kim
 */
public class CallMapper {

    public static List<CallInfo> toCallInfoList(List<Call> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
            .map(CallMapper::toCallInfo)
            .filter(Objects::nonNull)
            .collect(toList());
    }

    private static CallInfo toCallInfo(Call call) {
        if (call == null) {
            return null;
        }

        return CallInfo.builder()
            .id(call.getId())
            .passenger(call.getPassenger())
            .driver(call.getDriver())
            .address(call.getAddress())
            .requestDate(call.getRequestDate())
            .assignDate(call.getAssignDate())
            .status(call.getStatus())
            .build();
    }

}
