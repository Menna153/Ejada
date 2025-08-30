package com.example.account_service.config;

import com.example.account_service.constant.AccountConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder.name(AccountConstant.LOGGING_TOPIC).build();
    }
}
