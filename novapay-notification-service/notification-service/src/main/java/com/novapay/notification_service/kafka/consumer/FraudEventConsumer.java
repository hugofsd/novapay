package com.novapay.notification_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapay.notification_service.kafka.event.FraudDetectedEvent;
import com.novapay.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// Consumer 2 de 2 — escuta fraud-events publicado pelo fraud-service
// dois @KafkaListener ativos no mesmo serviço simultaneamente
// Spring cria um container de listener separado para cada um — rodam em paralelo
@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "fraud-events", groupId = "notification-fraud-group")
    public void consume(String payload) {
        try {
            FraudDetectedEvent event = objectMapper.readValue(payload, FraudDetectedEvent.class);
            log.info("FraudDetectedEvent recebido: transactionId={}, status={}",
                    event.getTransactionId(), event.getStatus());
            notificationService.notifyFraudDetected(event);
        } catch (Exception e) {
            log.error("Erro ao processar fraud-events. Payload: {}", payload, e);
        }
    }

}
