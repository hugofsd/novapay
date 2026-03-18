package com.novapay.account_service.kafka.producer;

import com.novapay.account_service.kafka.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventProducer {

    private static final String TOPIC = "account-events";

    private final KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate;

    public void publishAccountCreated(AccountCreatedEvent event) {
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
