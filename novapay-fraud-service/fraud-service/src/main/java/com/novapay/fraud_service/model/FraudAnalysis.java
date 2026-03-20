package com.novapay.fraud_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_analysis")
@Getter
@Setter
@NoArgsConstructor   // Obrigatório: Hibernate precisa de construtor vazio para instanciar
@AllArgsConstructor  // Usado pelo @Builder para preencher todos os campos
@Builder
public class FraudAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT do banco
    private Long id;

    // ID da transação do transaction-service — sem FK pois bancos são isolados
    @Column(nullable = false, unique = true)
    private Long transactionId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // EnumType.STRING: salva "APPROVED"/"SUSPICIOUS" no banco (não 0/1)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FraudStatus status;

    // null quando APPROVED, preenchido quando SUSPICIOUS
    @Column(length = 255)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;


}
