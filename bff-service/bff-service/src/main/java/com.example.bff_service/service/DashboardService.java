package com.example.bff_service.service;

import com.example.bff_service.constant.BFFConstant;
import com.example.bff_service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebClient.Builder webClient;

    private void sendLog(Object payload, String type) {
        try {
            LogMessage log = new LogMessage(
                    objectMapper.writeValueAsString(payload),
                    type,
                    Instant.now()
            );
            String jsonLog = objectMapper.writeValueAsString(log);
            kafkaTemplate.send(BFFConstant.LOGGING_TOPIC, jsonLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mono<DashboardResponse> getDashboard(String userId, String appName) {
        Map<String, String> requestPayload = Map.of(
                "userId", userId,
                "appName", appName
        );
        sendLog(requestPayload, "Request");

        // Call Users Service
        Mono<GetUserProfile> userMono = webClient.build().get()
                .uri("http://users-service/users/{userId}/profile", userId)
                .header("APP-NAME", appName)
                .retrieve()
                .bodyToMono(GetUserProfile.class)
                .doOnNext(user -> sendLog(user, "Response"))
                .onErrorResume(err -> {
                    System.err.println("❌ USERS-SERVICE call failed: " + err.getMessage());
                    return Mono.just(new GetUserProfile(userId, "unknown", "unknown@example.com", "Unknown", "User"));
                });

        // Call Account Service
        Mono<List<AccountInfo>> accountsMono = webClient.build().get()
                .uri("http://account-service/users/{userId}/accounts", userId)
                .header("APP-NAME", appName)
                .retrieve()
                .bodyToFlux(AccountInfo.class)
                .collectList()
                .doOnNext(accounts -> sendLog(accounts, "Response"))
                .onErrorResume(err -> {
                    System.err.println("❌ ACCOUNT-SERVICE call failed: " + err.getMessage());
                    return Mono.just(List.of());
                });

        // Aggregate users + accounts + transactions
        return Mono.zip(userMono, accountsMono)
                .flatMap(tuple -> {
                    GetUserProfile user = tuple.getT1();
                    List<AccountInfo> accounts = tuple.getT2();

                    // Fetch transactions for each account
                    List<Mono<AccountWithTransactions>> accountMonos = accounts.stream()
                            .map(account -> webClient.build().get()
                                    .uri("http://transaction-service/accounts/{accountId}/transactions", account.getAccountId())
                                    .header("APP-NAME", appName)
                                    .retrieve()
                                    .bodyToFlux(AllTransactionsResponse.class)
                                    .collectList()
                                    .map(transactions -> new AccountWithTransactions(account, transactions))
                                    .onErrorResume(err -> {
                                        System.err.println("❌ TRANSACTION-SERVICE call failed for account "
                                                + account.getAccountId() + ": " + err.getMessage());
                                        return Mono.just(new AccountWithTransactions(account, List.of()));
                                    })
                            ).toList();

                    return Mono.zip(accountMonos, results -> {
                        List<AccountWithTransactions> accountWithTxs = List.of(results).stream()
                                .map(obj -> (AccountWithTransactions) obj)
                                .toList();

                        DashboardResponse dashboard = new DashboardResponse(
                                user.getUserId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                accountWithTxs
                        );
                        sendLog(dashboard, "Response");
                        return dashboard;
                    });
                });
    }
}
