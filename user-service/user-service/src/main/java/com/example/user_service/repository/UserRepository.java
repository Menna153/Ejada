package com.example.user_service.repository;

import com.example.user_service.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users,String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Users findByUsername(String username);
    Users findByUserId(String userId);
}
