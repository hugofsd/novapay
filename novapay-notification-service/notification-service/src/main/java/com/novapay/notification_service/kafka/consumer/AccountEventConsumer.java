package com.novapay.notification_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapay.notification_service.kafka.event.AccountCreatedEvent;
import com.novapay.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// Consumer 1 de 2 — escuta account-events publicado pelo account-service
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // groupId diferente dos outros consumers — cada serviço tem seu próprio grupo
    // "notification-account-group" → independente do "fraud-group" do fraud-service
    // Kafka entrega a mesma mensagem para grupos diferentes (fan-out)
    @KafkaListener(topics = "account-events", groupId = "notification-account-group")
    public void consume(String payload) {
        try {
            AccountCreatedEvent event = objectMapper.readValue(payload, AccountCreatedEvent.class);
            log.info("AccountCreatedEvent recebido: accountId={}", event.getId());
            notificationService.notifyAccountCreated(event);
        } catch (Exception e) {
            log.error("Erro ao processar account-events. Payload: {}", payload, e);
        }
    }
}