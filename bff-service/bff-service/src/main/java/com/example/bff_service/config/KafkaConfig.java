package com.example.bff_service.config;

import com.example.bff_service.constant.BFFConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder.name(BFFConstant.LOGGING_TOPIC).build();
    }
}
