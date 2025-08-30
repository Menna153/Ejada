package com.example.transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
}
