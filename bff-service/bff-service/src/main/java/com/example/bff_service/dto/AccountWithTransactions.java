package com.example.bff_service.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithTransactions {
    private AccountInfo account;
    private List<AllTransactionsResponse> transactions;
}
