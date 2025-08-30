package com.example.account_service.dto;

import com.example.account_service.model.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private String userId;
    private AccountType accountType;
    private BigDecimal initialBalance;
}
