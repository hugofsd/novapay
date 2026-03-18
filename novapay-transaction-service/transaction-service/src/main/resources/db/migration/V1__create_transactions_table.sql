CREATE TABLE transactions (
    id                BIGINT         NOT NULL AUTO_INCREMENT,

    -- IDs das contas: não são FK reais para o banco do account-service
    -- porque cada serviço tem seu próprio banco (Database per Service pattern).
    -- A validação de existência da conta será feita via Kafka no futuro.
    source_account_id BIGINT         NOT NULL,
    target_account_id BIGINT         NOT NULL,

    amount            DECIMAL(19, 2) NOT NULL,

    -- TRANSFER: de conta para conta
    -- DEPOSIT:  entrada de dinheiro
    -- WITHDRAWAL: saída de dinheiro
    type              VARCHAR(20)    NOT NULL,

    -- PENDING: recém criada, aguardando processamento
    -- COMPLETED: processada com sucesso
    -- FAILED: rejeitada (ex: fraude detectada)
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING',

    created_at        DATETIME(6)    NOT NULL,

    PRIMARY KEY (id)
);