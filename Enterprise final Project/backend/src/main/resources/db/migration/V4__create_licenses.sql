CREATE TYPE license_status AS ENUM ('ACTIVE', 'EXPIRED');

CREATE TABLE licenses (
    id             BIGSERIAL      PRIMARY KEY,
    start_date     DATE           NOT NULL,
    end_date       DATE           NOT NULL CHECK (end_date >= start_date),
    terms          TEXT,
    status         license_status NOT NULL DEFAULT 'ACTIVE',
    movie_id       BIGINT         NOT NULL REFERENCES movies (id),
    distributor_id BIGINT         NOT NULL REFERENCES users (id),
    client_id      BIGINT         NOT NULL REFERENCES clients (id),
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
