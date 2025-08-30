package com.example.transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInitiationRequest {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String description;
}
