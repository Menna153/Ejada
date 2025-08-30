package com.example.transaction_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionExecutionRequest {
    private String transactionId;
}
