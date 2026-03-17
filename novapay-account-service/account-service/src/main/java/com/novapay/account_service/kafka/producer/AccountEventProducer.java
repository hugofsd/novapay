package com.novapay.account_service.kafka.producer;

import com.novapay.account_service.kafka.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// @Slf4j: gera automaticamente o campo "log" para usar log.info(), log.error(), etc.
// @Component: registra esta classe como bean gerenciado pelo Spring.
// @RequiredArgsConstructor: injeta KafkaTemplate pelo construtor (injeção recomendada).
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventProducer {

    // Nome do tópico. Outros serviços que quiserem ouvir devem assinar este mesmo nome.
    private static final String TOPIC = "account-eventes";

    // KafkaTemplate<String, AccountCreatedEvent>:
    //   - String: tipo da chave (usamos o id da conta como chave)
    //   - AccountCreatedEvent: tipo do valor (serializado como JSON pelo JsonSerializer)
    // O Spring Boot configura o KafkaTemplate automaticamente via application.properties.
    private final KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate;

    public void publishAccountCreated(AccountCreatedEvent event) {
        // send(tópico, chave, valor):
        //   - chave garante que eventos da mesma conta vão para a mesma partição Kafka
        //   - valor é serializado como JSON e enviado de forma assíncrona
        // .whenComplete() é callback assíncrono — não bloqueia a thread principal.
        // Importante: se o Kafka falhar, a conta JÁ FOI salva no banco.
        // Em produção, usaria outbox pattern para garantir consistência.
        kafkaTemplate.send(TOPIC, String.valueOf(event.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish AccountCreatedEvent id={}", event.getId(), ex);
                    } else {
                        log.info("AccountCreatedEvent published: id={}", event.getId());
                    }
                });
    }




}
