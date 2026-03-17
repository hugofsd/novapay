package com.novapay.account_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Este DTO é o que a API devolve — nunca devolva a entidade Account diretamente.
// Motivo: a entidade pode ter campos internos (ex: senha, versão de auditoria)
//         que não devem ser expostos na API.
// @Builder: permite criar o objeto com AccountResponse.builder().id(1L).build()
//           — muito mais legível do que um construtor com 7 parâmetros.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String ownerName;
    private String cpf;
    private BigDecimal balance;
    private String status;         // String, não enum — o consumidor da API recebe texto
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
