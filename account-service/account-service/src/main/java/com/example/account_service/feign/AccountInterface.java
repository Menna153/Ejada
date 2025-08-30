package com.example.account_service.feign;

import com.example.account_service.dto.GetUserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USERS-SERVICE")
public interface AccountInterface {
    @GetMapping("/users/{userId}/profile")
    public GetUserProfile getUserInfo(@PathVariable String userId);
}
