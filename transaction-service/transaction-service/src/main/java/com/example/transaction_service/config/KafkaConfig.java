package com.example.transaction_service.config;

import com.example.transaction_service.constant.TransactionConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name(TransactionConstant.LOGGING_TOPIC).build();
    }
}
