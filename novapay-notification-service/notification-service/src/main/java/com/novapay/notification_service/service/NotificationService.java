package com.novapay.notification_service.service;

import com.novapay.notification_service.dto.response.NotificationResponse;
import com.novapay.notification_service.kafka.event.AccountCreatedEvent;
import com.novapay.notification_service.kafka.event.FraudDetectedEvent;
import com.novapay.notification_service.model.Notification;
import com.novapay.notification_service.model.NotificationType;
import com.novapay.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    // Chamado pelo AccountEventConsumer
    @Transactional
    public void notifyAccountCreated(AccountCreatedEvent event) {
        Notification n = Notification.builder()
                .type(NotificationType.ACCOUNT_CREATED)
                .message("Conta criada para " + event.getOwnerName() + " (CPF: " + event.getCpf() + ")")
                .referenceId(event.getId())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(n);
        log.info("Notificação ACCOUNT_CREATED salva: accountId={}", event.getId());
    }

    // Chamado pelo FraudEventConsumer
    @Transactional
    public void notifyFraudDetected(FraudDetectedEvent event) {
        String message = "SUSPICIOUS".equals(event.getStatus())
                ? "Transação " + event.getTransactionId() + " suspeita: " + event.getReason()
                : "Transação " + event.getTransactionId() + " aprovada";

        Notification n = Notification.builder()
                .type(NotificationType.FRAUD_DETECTED)
                .message(message)
                .referenceId(event.getTransactionId())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(n);
        log.info("Notificação FRAUD_DETECTED salva: transactionId={}, status={}",
                event.getTransactionId(), event.getStatus());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {
        return repository.findAll().stream()
                .map(NotificationResponse::from)
                .toList();
    }


}
