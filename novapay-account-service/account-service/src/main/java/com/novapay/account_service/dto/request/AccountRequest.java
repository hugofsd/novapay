package com.novapay.account_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


// @Getter: gera getOwnerName() e getCpf() — o Spring usa esses getters para
//          serializar/deserializar o JSON. Sem @Getter, os campos ficam inacessíveis.
// @NoArgsConstructor: construtor vazio necessário para o Jackson (biblioteca JSON)
//                     instanciar o objeto ao deserializar o corpo da requisição.
// @AllArgsConstructor: construtor com todos os campos, útil nos testes.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    // @NotBlank: rejeita null, string vazia "" e string só de espaços "   ".
    // A validação é ativada pelo @Valid no AccountController.
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    // @Pattern: valida com regex. "\\d{11}" = exatamente 11 dígitos numéricos.
    // O CPF é guardado sem formatação: "12345678901", não "123.456.789-01".
    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "\\d{11}", message = "CPF must have exactly 11 digits")
    private String cpf;

}
