CREATE TYPE movie_availability_status AS ENUM ('AVAILABLE', 'RENTED');

CREATE TABLE movies (
    id                  BIGSERIAL                   PRIMARY KEY,
    title               VARCHAR(255)                NOT NULL,
    genre               VARCHAR(100)                NOT NULL,
    release_date        DATE                        NOT NULL,
    duration            INTEGER                     NOT NULL CHECK (duration > 0),
    description         TEXT,
    availability_status movie_availability_status   NOT NULL DEFAULT 'AVAILABLE',
    distributor_id      BIGINT                      NOT NULL REFERENCES users (id),
    created_at          TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
