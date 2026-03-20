package com.novapay.fraud_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapay.fraud_service.kafka.event.TransactionCreatedEvent;
import com.novapay.fraud_service.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// ── Por que isso é diferente do Producer? ────────────────────────────
// Producer: você chama kafkaTemplate.send() no momento que quiser
// Consumer: Spring chama este método AUTOMATICAMENTE quando chega
//           uma mensagem no tópico — você não faz polling manual
// ─────────────────────────────────────────────────────────────────────
//
// ── Por que recebemos String e não TransactionCreatedEvent direto? ────
// Spring Kafka 4.0 deprecou o JsonDeserializer como deserializador Kafka.
// A solução atual é usar StringDeserializer no Kafka (sem deprecation)
// e deserializar o JSON manualmente aqui com o ObjectMapper do Spring Boot.
//
// Vantagem: o ObjectMapper do Spring Boot já tem JavaTimeModule registrado
// automaticamente (via JacksonAutoConfiguration), então LocalDateTime
// funciona sem nenhuma configuração extra.
// ─────────────────────────────────────────────────────────────────────
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final FraudAnalysisService fraudAnalysisService;

    // ObjectMapper injetado do contexto Spring Boot
    // Spring Boot registra JavaTimeModule automaticamente ao detectar
    // jackson-datatype-jsr310 no classpath (vem via spring-boot-starter-web)
    // Por isso LocalDateTime em TransactionCreatedEvent funciona sem config extra
    private final ObjectMapper objectMapper;

    // topics: mesmo tópico que TransactionEventProducer.TOPIC no transaction-service
    //
    // groupId = "fraud-group":
    //   Identifica este consumer no broker
    //   Se subir 2 instâncias do fraud-service com o mesmo groupId,
    //   Kafka divide as partições entre elas automaticamente (load balance)
    //
    // payload é String porque configuramos StringDeserializer no application.properties
    @KafkaListener(topics = "transaction-events", groupId = "fraud-group")
    public void consume(String payload) {
        try {
            // Converte o JSON string recebido do Kafka para o objeto de evento
            // ObjectMapper do Spring Boot já conhece LocalDateTime via JavaTimeModule
            TransactionCreatedEvent event = objectMapper.readValue(payload, TransactionCreatedEvent.class);

            log.info("TransactionCreatedEvent recebido: transactionId={}, amount={}",
                    event.getId(), event.getAmount());

            // Consumer deve ser fino: receber → deserializar → logar → delegar
            // Toda a lógica de análise fica no Service
            fraudAnalysisService.analyze(event);

        } catch (Exception e) {
            // Log do payload original facilita debug (visível no Kafka UI também)
            log.error("Erro ao processar evento do tópico transaction-events. Payload: {}", payload, e);
        }
    }
}
