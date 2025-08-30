package com.example.user_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {
    private String userId;
    private String username;
    private String message;
}
