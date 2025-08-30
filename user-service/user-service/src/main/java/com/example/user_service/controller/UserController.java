package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse registerUser(@RequestBody CreateUserRequest user){
        return userService.registerUser(user);
    }

    @PostMapping("/users/login")
    public LoginResponse loginUser(@RequestBody LoginRequest user){
        return userService.loginUser(user);
    }

    @GetMapping("/users/{userId}/profile")
    public GetUserProfile getUserInfo(@PathVariable String userId) {
        return userService.getUserInfo(userId);
    }
}
