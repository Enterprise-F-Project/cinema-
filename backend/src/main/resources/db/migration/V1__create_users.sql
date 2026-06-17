CREATE TYPE user_role AS ENUM ('ADMIN', 'DISTRIBUTOR', 'CLIENT');

CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        user_role    NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
