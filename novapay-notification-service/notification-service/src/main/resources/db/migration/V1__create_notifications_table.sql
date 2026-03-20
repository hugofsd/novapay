CREATE TABLE notifications (

    id              BIGINT       NOT NULL AUTO_INCREMENT,
    -- Tipo: ACCOUNT_CREATED ou FRAUD_DETECTED
    type            VARCHAR(30)  NOT NULL,

    -- Mensagem legível gerada pelo service
    message         VARCHAR(500) NOT NULL,

    -- ID da entidade de origem (accountId ou transactionId)
    -- Sem FK — bancos isolados (Database per Service)
    reference_id    BIGINT       NOT NULL,

    created_at      DATETIME     NOT NULL,

    PRIMARY KEY (id)
);
