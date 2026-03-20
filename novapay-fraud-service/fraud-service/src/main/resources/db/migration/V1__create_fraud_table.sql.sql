CREATE TABLE fraud_analysis (
    id             BIGINT         NOT NULL AUTO_INCREMENT,

    -- ID da transação vinda do transaction-service via Kafka
    -- NÃO é FK: bancos são isolados (Database per Service pattern)
    transaction_id BIGINT         NOT NULL,

    -- Snapshot do valor no momento da análise
    amount         DECIMAL(15, 2) NOT NULL,

    -- Resultado: APPROVED ou SUSPICIOUS
    -- VARCHAR em vez de ENUM MySQL (mais fácil de alterar no futuro)
    status         VARCHAR(20)    NOT NULL,

    -- Preenchido só quando SUSPICIOUS: "High amount" | "Same account"
    -- NULL quando APPROVED
    reason         VARCHAR(255),

    analyzed_at    DATETIME       NOT NULL,

    PRIMARY KEY (id),

    -- Uma transação só pode ter uma análise
    UNIQUE KEY uq_fraud_transaction (transaction_id)
);
