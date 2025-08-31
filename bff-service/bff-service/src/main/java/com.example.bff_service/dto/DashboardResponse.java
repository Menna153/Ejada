package com.example.bff_service.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<AccountWithTransactions> accounts;
}
