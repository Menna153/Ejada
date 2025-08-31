package com.example.transaction_service.feign;

import com.example.transaction_service.dto.AccountInfo;
import com.example.transaction_service.dto.MessageResponse;
import com.example.transaction_service.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("ACCOUNT-SERVICE")
public interface TransactionInterface {
    @GetMapping("/accounts/{accountId}")
    public AccountInfo getAccount(@PathVariable String accountId);
    @PutMapping("/accounts/transfer")
    public MessageResponse transfer(@RequestBody TransferRequest transferRequest);
}
