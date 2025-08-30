package com.example.user_service.service;

import com.example.user_service.constant.UserConstant;
import com.example.user_service.dto.*;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.model.Users;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void sendLog(Object payload, String type) {
        try {
            LogMessage log = new LogMessage(
                    objectMapper.writeValueAsString(payload),
                    type,
                    Instant.now()
            );
            String jsonLog = objectMapper.writeValueAsString(log);
            kafkaTemplate.send(UserConstant.LOGGING_TOPIC, jsonLog);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize log message", e);
        }
    }

    @Transactional
    public CreateUserResponse registerUser(CreateUserRequest createUserRequest){
        sendLog(createUserRequest, "Request");
        if (userRepository.existsByUsername(createUserRequest.getUsername()) ||
                userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username or email already exists."
            );
        }
        String hashedPassword = passwordEncoder.encode(createUserRequest.getPassword());
        Users user = userMapper.fromCreateUserRequest(createUserRequest);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        CreateUserResponse createUserResponse = userMapper.toCreateUserResponse(user);
        createUserResponse.setMessage("User registered successfully.");
        sendLog(createUserResponse, "Response");
        return createUserResponse;
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        sendLog(loginRequest, "Request");
        Users user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        LoginResponse loginResponse = userMapper.toLoginResponse(user);
        sendLog(loginResponse, "Response");
        return loginResponse;
    }

    public GetUserProfile getUserInfo(String userId) {
        sendLog(userId, "Request");
        Users user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }
        GetUserProfile getUserProfile = userMapper.toUserProfile(user);
        sendLog(getUserProfile, "Response");
        return getUserProfile;
    }

}
