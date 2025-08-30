package com.example.transaction_service.mapper;

import com.example.transaction_service.dto.AllTransactionsResponse;
import com.example.transaction_service.dto.TransactionInitiationRequest;
import com.example.transaction_service.dto.TransactionResponse;
import com.example.transaction_service.dto.TransferRequest;
import com.example.transaction_service.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransactionMapper {
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deliveryStatus", ignore = true)
    @Mapping(target = "amount", source = "amount")
    Transaction toTransaction(TransactionInitiationRequest transactionInitiationRequest);

    TransactionResponse toTransferResponse(Transaction transaction);

    TransferRequest toTransferRequest(TransactionInitiationRequest transactionInitiationRequest);

    TransferRequest fromTransaction(Transaction transaction);

    AllTransactionsResponse toAllTransactionsResponse(Transaction transaction);
}
