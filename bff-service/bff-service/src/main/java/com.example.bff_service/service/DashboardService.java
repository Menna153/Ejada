package com.example.bff_service.service;

import com.example.bff_service.constant.BFFConstant;
import com.example.bff_service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private WebClient.Builder webClientBuilder;

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
            kafkaTemplate.send(BFFConstant.LOGGING_TOPIC, jsonLog);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize log message", e);
        }
    }

    public Mono<DashboardResponse> getDashboard(String userId, String appName) {
        Map<String, String> requestPayload = Map.of(
                "userId", userId,
                "appName", appName
        );
        sendLog(requestPayload, "Request");
        WebClient webClient = webClientBuilder.build();
        Mono<GetUserProfile> userMono = webClient.get()
                .uri("http://localhost:8080/users/{userId}/profile", userId)
                .header("APP-NAME", appName)
                .retrieve()
                .bodyToMono(GetUserProfile.class)
                .doOnNext(user -> sendLog(user, "Response"));


        // Call Account Service
        Mono<List<AccountInfo>> accountsMono = webClient.get()
                .uri("http://localhost:8081/users/{userId}/accounts", userId)
                .header("APP-NAME", appName)
                .retrieve()
                .bodyToFlux(AccountInfo.class)
                .collectList()
                .doOnNext(accounts -> sendLog(accounts, "Response"));

        // Aggregate accounts + transactions
        return Mono.zip(userMono, accountsMono)
                .flatMap(tuple -> {
                    GetUserProfile user = tuple.getT1();
                    List<AccountInfo> accounts = tuple.getT2();

                    List<Mono<AccountWithTransactions>> accountMonos = accounts.stream()
                            .map(account -> webClient.get()
                                    .uri("http://localhost:8082/accounts/{accountId}/transactions", account.getAccountId())
                                    .header("APP-NAME", appName)
                                    .retrieve()
                                    .bodyToFlux(AllTransactionsResponse.class)
                                    .collectList()
                                    .map(transactions -> {
                                        sendLog(transactions, "Response"); // log each account's transactions
                                        return new AccountWithTransactions(account, transactions);
                                    })
                            ).toList();

                    return Mono.zip(accountMonos, array -> {
                        List<AccountWithTransactions> accountWithTxs = Arrays.stream(array)
                                .map(obj -> (AccountWithTransactions) obj)
                                .toList();
                        DashboardResponse dashboard = new DashboardResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), accountWithTxs);
                        sendLog(dashboard, "Response");
                        return dashboard;
                    });
                });
    }
}
