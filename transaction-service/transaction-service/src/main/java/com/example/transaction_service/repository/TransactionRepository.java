package com.example.transaction_service.repository;

import com.example.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
        List<Transaction> findByFromAccountId(String fromAccountId);
        List<Transaction> findByToAccountId(String toAccountId);
}
