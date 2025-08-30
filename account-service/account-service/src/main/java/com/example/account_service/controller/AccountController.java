package com.example.account_service.controller;

import com.example.account_service.dto.*;
import com.example.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts/{accountId}")
    public AccountInfo getAccount(@PathVariable String accountId) {
        return accountService.getAccount(accountId);
    }

    @PutMapping("/accounts/transfer")
    public MessageResponse transfer(@RequestBody TransferRequest transferRequest) {
        return accountService.transfer(transferRequest);
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest createRequest) {
        return accountService.createAccount(createRequest);
    }

    @GetMapping("/users/{userId}/accounts")
    public List<AccountInfo> getAccounts(@PathVariable String userId) {
        return accountService.getAllAccounts(userId);
    }
}
