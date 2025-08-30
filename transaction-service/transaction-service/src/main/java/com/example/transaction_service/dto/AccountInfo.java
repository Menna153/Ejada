package com.example.transaction_service.dto;

import com.example.transaction_service.model.AccountStatus;
import com.example.transaction_service.model.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private String accountId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
}
