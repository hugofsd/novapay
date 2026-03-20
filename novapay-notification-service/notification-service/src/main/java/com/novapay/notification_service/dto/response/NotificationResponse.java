package com.novapay.notification_service.dto.response;

import com.novapay.notification_service.model.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private Long id;
    private String type;
    private String message;
    private Long referenceId;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .build();
    }

}
