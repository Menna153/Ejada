package com.example.account_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;
    private String userId;
    @Column(unique = true, nullable = false)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Instant timestamp;
}
