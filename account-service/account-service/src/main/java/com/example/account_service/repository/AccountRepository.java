package com.example.account_service.repository;

import com.example.account_service.model.Account;
import com.example.account_service.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
    List<Account> findByUserId(String userId);
    List<Account> findAllByStatusAndTimestampBefore(AccountStatus status, Instant timestamp);
}
