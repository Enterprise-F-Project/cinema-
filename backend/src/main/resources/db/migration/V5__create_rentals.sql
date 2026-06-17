CREATE TYPE rental_status AS ENUM ('REQUESTED', 'ACTIVE', 'COMPLETED');

CREATE TABLE rentals (
    id           BIGSERIAL     PRIMARY KEY,
    rental_date  TIMESTAMP     NOT NULL,
    return_date  TIMESTAMP     NOT NULL CHECK (return_date >= rental_date),
    status       rental_status NOT NULL DEFAULT 'REQUESTED',
    movie_id     BIGINT        NOT NULL REFERENCES movies (id),
    client_id    BIGINT        NOT NULL REFERENCES clients (id),
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
