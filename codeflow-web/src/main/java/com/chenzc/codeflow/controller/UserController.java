package com.chenzc.codeflow.controller;

import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.User;
import jakarta.annotation.Resource;
import org.chenzc.codeflow.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refactor/api")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public BasicResult login(@RequestBody User user) {

        return userService.login(user);
    }
}