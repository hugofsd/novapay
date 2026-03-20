package com.novapay.notification_service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // Registra suporte a LocalDateTime, LocalDate, etc.
                // Necessário porque este @Bean substitui o ObjectMapper auto-configurado do Spring Boot
                .registerModule(new JavaTimeModule())
                // Serializa LocalDateTime como "2026-03-20T19:33:42" em vez de array numérico
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
