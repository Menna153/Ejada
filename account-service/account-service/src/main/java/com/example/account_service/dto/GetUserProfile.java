package com.example.account_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserProfile {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
