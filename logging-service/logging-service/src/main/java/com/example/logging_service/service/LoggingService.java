package com.example.logging_service.service;

import com.example.logging_service.Respository.LogRepository;
import com.example.logging_service.constant.LoggingConstant;
import com.example.logging_service.dto.LogMessage;
import com.example.logging_service.model.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = LoggingConstant.LOGGING_TOPIC, groupId = "logging-service-group")
    public void consumeLog(String message) {
        try {
            // Parse JSON log
            LogMessage logMessage = objectMapper.readValue(message, LogMessage.class);

            // Save to DB
            Log log = new Log();
            log.setMessage(logMessage.getMessage());
            log.setMessageType(logMessage.getMessageType());
            log.setDateTime(logMessage.getDateTime());

            logRepository.save(log);

            System.out.println("✅ Log saved: " + log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
