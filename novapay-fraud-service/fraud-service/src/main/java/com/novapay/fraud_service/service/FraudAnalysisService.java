package com.novapay.fraud_service.service;

import com.novapay.fraud_service.exception.FraudNotFoundException;
import com.novapay.fraud_service.kafka.event.FraudDetectedEvent;
import com.novapay.fraud_service.kafka.event.TransactionCreatedEvent;
import com.novapay.fraud_service.kafka.producer.FraudEventProducer;
import com.novapay.fraud_service.model.FraudAnalysis;
import com.novapay.fraud_service.model.FraudStatus;
import com.novapay.fraud_service.repository.FraudAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("1000");

    private final FraudAnalysisRepository repository;
    private final FraudEventProducer producer;

    // Chamado pelo consumer ao receber evento Kafka
    // @Transactional: save() só comita se não houver exceção
    @Transactional
    public void analyze(TransactionCreatedEvent event) {

        // Idempotência: Kafka garante at-least-once (pode chegar duplicado)
        // Se já analisou, ignora silenciosamente
        if (repository.findByTransactionId(event.getId()).isPresent()) {
            log.warn("Transação {} já foi analisada, ignorando duplicata", event.getId());
            return;
        }

        // ── Regras de fraude (em ordem de prioridade) ──────────────
        FraudStatus status;
        String reason = null;

        // Regra 1: valor alto
        if (event.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            status = FraudStatus.SUSPICIOUS;
            reason = "High amount";

            // Regra 2: conta origem == conta destino
        } else if (event.getSourceAccountId() != null
                && event.getSourceAccountId().equals(event.getTargetAccountId())) {
            status = FraudStatus.SUSPICIOUS;
            reason = "Same account";

            // Sem risco detectado
        } else {
            status = FraudStatus.APPROVED;
        }
        // ───────────────────────────────────────────────────────────

        FraudAnalysis analysis = FraudAnalysis.builder()
                .transactionId(event.getId())
                .amount(event.getAmount())
                .status(status)
                .reason(reason)
                .analyzedAt(LocalDateTime.now())
                .build();

        repository.save(analysis);
        log.info("Análise salva: transactionId={}, status={}, reason={}",
                event.getId(), status, reason);

        // Publica resultado para o notification-service (Dia 6)
        producer.publishFraudDetected(FraudDetectedEvent.builder()
                .transactionId(analysis.getTransactionId())
                .amount(analysis.getAmount())
                .status(analysis.getStatus().name())
                .reason(analysis.getReason())
                .analyzedAt(analysis.getAnalyzedAt())
                .build());
    }

    // Chamado pelo controller: GET /fraud/{transactionId}
    // readOnly=true: Hibernate não faz dirty check (otimização de leitura)
    @Transactional(readOnly = true)
    public FraudAnalysis findByTransactionId(Long transactionId) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new FraudNotFoundException(transactionId));
    }


}
