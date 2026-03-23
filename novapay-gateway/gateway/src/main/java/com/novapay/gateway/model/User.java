package com.novapay.gateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login único — não permite dois usuários com mesmo username
    @Column(unique = true, nullable = false)
    private String username;

    // Sempre armazenado com hash BCrypt — nunca em plain text
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Executado automaticamente antes de qualquer INSERT
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
