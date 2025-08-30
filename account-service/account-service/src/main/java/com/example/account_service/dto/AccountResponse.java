package com.example.account_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String accountId;
    private String accountNumber;
    private String message;
}
