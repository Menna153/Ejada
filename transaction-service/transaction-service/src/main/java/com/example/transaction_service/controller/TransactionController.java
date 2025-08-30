package com.example.transaction_service.controller;

import com.example.transaction_service.dto.AllTransactionsResponse;
import com.example.transaction_service.dto.TransactionExecutionRequest;
import com.example.transaction_service.dto.TransactionInitiationRequest;
import com.example.transaction_service.dto.TransactionResponse;
import com.example.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions/transfer/initiation")
    public TransactionResponse transactionInitiation(@RequestBody TransactionInitiationRequest transactionInitiationRequest){
        return transactionService.initiateTransfer(transactionInitiationRequest);
    }

    @PostMapping("/transactions/transfer/execution")
    public TransactionResponse transactionExecution(@RequestBody TransactionExecutionRequest transactionExecutionRequest){
        return transactionService.executeTransfer(transactionExecutionRequest);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<AllTransactionsResponse> getAllTransactions(@PathVariable("accountId") String accountId){
        return transactionService.getAllTransactions(accountId);
    }
}
