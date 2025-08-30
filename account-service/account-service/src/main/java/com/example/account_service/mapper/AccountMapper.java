package com.example.account_service.mapper;

import com.example.account_service.dto.AccountInfo;
import com.example.account_service.dto.AccountResponse;
import com.example.account_service.dto.CreateAccountRequest;
import com.example.account_service.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {
    AccountInfo fromAccount(Account account);

    @Mapping(target = "message", ignore = true)
    AccountResponse toAccountResponse(Account account);

    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    Account fromCreateAccount(CreateAccountRequest request);
}
