package com.example.transaction_service.dto;

import com.example.transaction_service.model.TransactionStatus;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private TransactionStatus status;
    private Instant timestamp;
}
