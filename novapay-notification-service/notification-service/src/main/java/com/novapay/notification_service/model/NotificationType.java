package com.novapay.notification_service.model;

public enum NotificationType {
    ACCOUNT_CREATED,   // disparado quando account-service publica em account-events
    FRAUD_DETECTED     // disparado quando fraud-service publica em fraud-events
}
