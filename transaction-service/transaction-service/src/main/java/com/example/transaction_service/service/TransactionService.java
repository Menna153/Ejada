package com.example.transaction_service.service;

import com.example.transaction_service.constant.TransactionConstant;
import com.example.transaction_service.dto.*;
import com.example.transaction_service.feign.TransactionInterface;
import com.example.transaction_service.mapper.TransactionMapper;
import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.transaction_service.dto.LogMessage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.example.transaction_service.model.DeliveryStatus.*;
import static com.example.transaction_service.model.TransactionStatus.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionInterface transactionInterface;

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
            kafkaTemplate.send(TransactionConstant.LOGGING_TOPIC, jsonLog);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize log message", e);
        }
    }

    @Transactional
    public TransactionResponse initiateTransfer(TransactionInitiationRequest transactionInitiationRequest) {
        sendLog(transactionInitiationRequest, "Request");
        TransferRequest transferRequest = transactionMapper.toTransferRequest(transactionInitiationRequest);
        AccountInfo accountFromInfo = transactionInterface.getAccount(transferRequest.getFromAccountId());
        AccountInfo accountToInfo = transactionInterface.getAccount(transferRequest.getToAccountId());
        BigDecimal balance = accountFromInfo.getBalance();
        if (accountFromInfo.getBalance().compareTo(transactionInitiationRequest.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        Transaction transaction = transactionMapper.toTransaction(transactionInitiationRequest);
        transaction.setStatus(INITIATED);
        transaction.setDeliveryStatus(SENT);
        transactionRepository.save(transaction);
        TransactionResponse transactionResponse = transactionMapper.toTransferResponse(transaction);
        sendLog(transactionResponse, "Response");
        return transactionResponse;
    }

    public TransactionResponse executeTransfer(TransactionExecutionRequest transactionExecutionRequest) {
        sendLog(transactionExecutionRequest, "Request");
        Transaction transaction = transactionRepository.findById(transactionExecutionRequest.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not initiated"));
        TransferRequest transferRequest = transactionMapper.fromTransaction(transaction);

        try{
            MessageResponse messageResponse = transactionInterface.transfer(transferRequest);
            transaction.setStatus(SUCCESS);
            transaction.setDeliveryStatus(DELIVERED);
            transactionRepository.save(transaction);
        } catch (ResponseStatusException ex) {
            transaction.setStatus(FAILED);
            transaction.setDeliveryStatus(SENT);
            transactionRepository.save(transaction);
            throw ex;
        } catch (Exception e) {
            transaction.setStatus(FAILED);
            transaction.setDeliveryStatus(SENT);
            transactionRepository.save(transaction);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while processing transfer");
        }
        TransactionResponse transactionResponse = transactionMapper.toTransferResponse(transaction);
        sendLog(transactionResponse, "Response");
        return transactionResponse;
    }

    public List<AllTransactionsResponse> getAllTransactions(String accountId) {
        sendLog(accountId, "Request");
        List<Transaction> transactions = transactionRepository.findByFromAccountId(accountId);
        List<Transaction> transactionsTo = transactionRepository.findByToAccountId(accountId);
        transactions.addAll(transactionsTo);
        sendLog(transactions, "Response");
        return transactions.stream()
                .map(transactionMapper::toAllTransactionsResponse)
                .toList();
    }
}
