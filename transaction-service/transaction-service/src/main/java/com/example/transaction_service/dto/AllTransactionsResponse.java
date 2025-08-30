package com.example.transaction_service.dto;

import com.example.transaction_service.model.DeliveryStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllTransactionsResponse {
    private String transactionId;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String description;
    private Instant timestamp;
    private DeliveryStatus deliveryStatus;
}
