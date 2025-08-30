package com.example.user_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    private String password;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    private String firstName;
    private String lastName;
}
