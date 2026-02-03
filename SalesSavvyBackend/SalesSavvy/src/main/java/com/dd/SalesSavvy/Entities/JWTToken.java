package com.dd.SalesSavvy.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Customer customer;

    String token;
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    public JWTToken(Customer customer, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.customer = customer;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
