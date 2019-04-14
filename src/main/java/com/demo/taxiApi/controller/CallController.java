package com.demo.taxiApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.taxiApi.domain.User;
import com.demo.taxiApi.model.AuthInfo;
import com.demo.taxiApi.service.CallService;

/**
 * @author yunsung Kim
 */
@RestController
@RequestMapping("/api/call")
public class CallController {

    @Autowired
    private CallService callService;

    @GetMapping("/list")
    public AuthInfo list(@RequestBody User user) {
        callService.list();
        return null;
    }

    @GetMapping("/request")
    public AuthInfo request(@RequestBody User user) {
        return null;
    }

    @GetMapping("/assign")
    public AuthInfo assign(@RequestBody User user) {
        return null;
    }
}
