package com.example.bff_service.controller;

import com.example.bff_service.dto.DashboardResponse;
import com.example.bff_service.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public Mono<DashboardResponse> getDashboard(
            @PathVariable String userId,
            @RequestHeader("APP-NAME") String appName) {
        return dashboardService.getDashboard(userId, appName)
                .onErrorMap(WebClientResponseException.NotFound.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .onErrorMap(Exception.class,
                        ex -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Failed to retrieve dashboard data due to an issue with downstream services"));
    }

}
