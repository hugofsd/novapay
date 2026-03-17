package com.novapay.account_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// @Entity: diz ao JPA que esta classe representa uma tabela no banco.
// @Table(name = "accounts"): define o nome exato da tabela (criada pelo Flyway no V1__.sql).
// @Getter / @Setter: Lombok gera todos os getters e setters automaticamente.
// @NoArgsConstructor: Lombok gera o construtor vazio — obrigatório para o JPA funcionar.
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    // @Id: chave primária da tabela.
    // @GeneratedValue IDENTITY: o banco auto-incrementa o valor (1, 2, 3...).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // name = "owner_name": o campo Java chama ownerName (camelCase),
    // mas no banco a coluna se chama owner_name (snake_case).
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    // unique = true: o banco rejeita CPF duplicado (constraint de unicidade).
    // length = 11: CPF tem exatamente 11 dígitos, sem formatação.
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    // BigDecimal para dinheiro: nunca use double/float para valores monetários
    // (problemas de arredondamento). precision=19 dígitos totais, scale=2 decimais.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // @Enumerated(EnumType.STRING): salva "ACTIVE" no banco em vez de 0, 1, 2.
    // STRING é preferível a ORDINAL porque não quebra se você mudar a ordem do enum.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    // updatable = false: o JPA nunca atualiza esta coluna após o INSERT.
    // Garante que a data de criação nunca muda.
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // @PrePersist: executado pelo JPA automaticamente ANTES do INSERT no banco.
    // Centraliza os valores padrão aqui — o service não precisa se preocupar.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // momento exato da criação
        updatedAt = LocalDateTime.now();  // começa igual ao createdAt
        if (balance == null) balance = BigDecimal.ZERO;   // saldo inicial sempre zero
        if (status == null) status = AccountStatus.ACTIVE; // toda conta nasce ativa
    }

    // @PreUpdate: executado pelo JPA automaticamente ANTES de cada UPDATE no banco.
    // Assim o updatedAt é sempre atualizado sem você precisar lembrar de setar no service.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
