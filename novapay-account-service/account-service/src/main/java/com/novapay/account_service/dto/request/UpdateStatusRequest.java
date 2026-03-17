package com.novapay.account_service.dto.request;

import com.novapay.account_service.model.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// DTO exclusivo para o endpoint PATCH /accounts/{id}/status.
// Recebe apenas o novo status — não permite alterar outros campos da conta.
// Essa granularidade é intencional: cada endpoint faz uma coisa só.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    // @NotNull (não @NotBlank): o campo é um enum, não uma String.
    // Se o JSON enviar um valor inválido (ex: "DELETADO"), o Jackson lança erro 400
    // antes mesmo de chegar no @NotNull.
    @NotNull(message = "Status is required")
    private AccountStatus status;

}
