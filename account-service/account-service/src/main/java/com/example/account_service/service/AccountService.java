package com.example.account_service.service;

import com.example.account_service.constant.AccountConstant;
import com.example.account_service.dto.*;
import com.example.account_service.feign.AccountInterface;
import com.example.account_service.mapper.AccountMapper;
import com.example.account_service.model.Account;
import com.example.account_service.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.account_service.model.AccountStatus.*;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountInterface accountInterface;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private void sendLog(Object payload, String type) {
        try {
            LogMessage log = new LogMessage(
                    objectMapper.writeValueAsString(payload),
                    type,
                    Instant.now()
            );
            String jsonLog = objectMapper.writeValueAsString(log);
            kafkaTemplate.send(AccountConstant.LOGGING_TOPIC, jsonLog);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize log message", e);
        }
    }

    public AccountInfo getAccount(String accountId) {
        sendLog(accountId, "Request");
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with ID " + accountId + " not found."));
        AccountInfo accountInfo = accountMapper.fromAccount(account);
        sendLog(accountInfo, "Response");
        return accountInfo;
    }

    @Transactional
    public MessageResponse transfer(TransferRequest request) {
        sendLog(request, "Request");
        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "From account not found"));
        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "To account not found"));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        MessageResponse response = new MessageResponse("Account updated successfully.");
        sendLog(response, "Response");
        return response;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        sendLog(request, "Request");
        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid initial balance");
        }
        try {
            accountInterface.getUserInfo(request.getUserId());
        } catch (ResponseStatusException ex) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while processing transfer");
        }
        Account account = accountMapper.fromCreateAccount(request);
        account.setStatus(ACTIVE);
        account.setAccountNumber(UUID.randomUUID().toString().substring(0, 10));
        accountRepository.save(account);

        AccountResponse response = accountMapper.toAccountResponse(account);
        response.setMessage("Account created successfully.");
        sendLog(response, "Response");
        return response;
    }

    public List<AccountInfo> getAllAccounts(String userId) {
        sendLog(userId, "Request");
        accountInterface.getUserInfo(userId);
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<AccountInfo> result = accounts.stream()
                .map(accountMapper::fromAccount)
                .toList();
        sendLog(result, "Response");
        return result;
    }
}
