CREATE TABLE clients (
    id                BIGSERIAL    PRIMARY KEY,
    organization_name VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL UNIQUE,
    phone             VARCHAR(20),
    address           TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
