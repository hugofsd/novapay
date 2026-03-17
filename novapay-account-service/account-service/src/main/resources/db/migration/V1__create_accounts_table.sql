CREATE TABLE accounts (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    owner_name VARCHAR(255) NOT NULL,
    cpf        VARCHAR(11)  NOT NULL UNIQUE,
    balance    DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
);
