package com.novapay.fraud_service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Registra o ObjectMapper como bean Spring para ser injetado no TransactionEventConsumer
// O consumer recebe o payload Kafka como String e usa este bean para deserializar
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                // Ignora campos do JSON que não existem na classe Java
                // Permite que o producer adicione campos novos sem quebrar o consumer
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
