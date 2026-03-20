package com.novapay.notification_service.repository;

import com.novapay.notification_service.model.Notification;
import com.novapay.notification_service.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByType(NotificationType type);

    List<Notification> findByReferenceId(Long referenceId);



}
