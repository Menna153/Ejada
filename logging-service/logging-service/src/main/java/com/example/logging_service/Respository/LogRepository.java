package com.example.logging_service.Respository;

import com.example.logging_service.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
