package com.novapay.transaction_service.kafka.producer;

import com.novapay.transaction_service.kafka.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// @Slf4j: gera o campo "log" automaticamente — evita criar Logger manualmente.
// @Component: registrado como bean pelo Spring — pode ser injetado em qualquer lugar.
// @RequiredArgsConstructor: injeta KafkaTemplate pelo construtor (injeção recomendada).
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventProducer {


    // Tópico onde os eventos de transação serão publicados.
    // fraud-service e notification-service vão se inscrever neste tópico.
    private static final String TOPIC = "transaction-events";


    // KafkaTemplate<String, TransactionCreatedEvent>:
    //   - String: tipo da chave (usamos o id da transação)
    //   - TransactionCreatedEvent: tipo do valor (serializado como JSON)
    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public void publishTransactionCreated(TransactionCreatedEvent event) {
        // send(tópico, chave, valor):
        //   - chave = id garante que eventos da mesma transação vão para a mesma partição
        //   - envio é assíncrono — não bloqueia a thread enquanto o Kafka processa
        // .whenComplete() é callback: só executa quando o Kafka confirmar o recebimento.
        // Se o Kafka falhar, a transação JÁ FOI salva no banco — o log de erro é crítico.
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
