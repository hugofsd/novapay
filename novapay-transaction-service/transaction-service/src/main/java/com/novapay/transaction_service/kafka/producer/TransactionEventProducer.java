package com.novapay.transaction_service.kafka.producer;

import com.novapay.transaction_service.kafka.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventProducer {

    private static final String TOPIC = "transaction-events";

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public void publishTransactionCreated(TransactionCreatedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar TransactionCreatedEvent id={}", event.getId(), ex);
                    } else {
                        log.info("TransactionCreatedEvent publicado: id={}", event.getId());
                    }
                });
    }
}
