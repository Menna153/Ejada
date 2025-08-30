package com.example.transaction_service.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogMessage {
    private String message;
    private String messageType;
    private Instant dateTime;
}
