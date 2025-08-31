package com.example.account_service.scheduler;

import com.example.account_service.model.Account;
import com.example.account_service.model.AccountStatus;
import com.example.account_service.repository.AccountRepository;
import com.example.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
public class ScheculedTask {
    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000) // 1 hour in milliseconds
    public void inactivateStaleAccounts() {
        Instant threshold = Instant.now().minus(24, ChronoUnit.HOURS);

        // Fetch all active accounts that are stale
        List<Account> staleAccounts = accountRepository.findAllByStatusAndTimestampBefore(AccountStatus.ACTIVE, threshold);

        for (Account account : staleAccounts) {
            account.setStatus(AccountStatus.INACTIVE);
        }

        accountRepository.saveAll(staleAccounts);
        System.out.println("Inactivated " + staleAccounts.size() + " stale accounts.");
    }
}
