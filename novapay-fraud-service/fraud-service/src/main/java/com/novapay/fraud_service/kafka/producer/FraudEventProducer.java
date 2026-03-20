package com.novapay.fraud_service.kafka.producer;


import com.novapay.fraud_service.kafka.event.FraudDetectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEventProducer {

    private static final String TOPIC = "fraud-events";

    // Spring auto-configura com base no application.properties (producer.value-serializer)
    private final KafkaTemplate<String, FraudDetectedEvent> kafkaTemplate;

    public void publishFraudDetected(FraudDetectedEvent event) {
        // Chave = transactionId → eventos da mesma transação vão para a mesma partição
        kafkaTemplate.send(TOPIC, String.valueOf(event.getTransactionId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // Log sem relançar: análise já foi salva no banco, não perder
                        log.error("Falha ao publicar FraudDetectedEvent transactionId={}",
                                event.getTransactionId(), ex);
                    } else {
                        log.info("FraudDetectedEvent publicado: transactionId={}, status={}",
                                event.getTransactionId(), event.getStatus());
                    }
                });
    }
}
