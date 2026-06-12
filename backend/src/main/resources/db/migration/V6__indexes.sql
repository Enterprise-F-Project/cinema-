-- Movies
CREATE INDEX idx_movies_distributor_id      ON movies (distributor_id);
CREATE INDEX idx_movies_genre               ON movies (genre);
CREATE INDEX idx_movies_availability_status ON movies (availability_status);

-- Licenses
CREATE INDEX idx_licenses_movie_id       ON licenses (movie_id);
CREATE INDEX idx_licenses_distributor_id ON licenses (distributor_id);
CREATE INDEX idx_licenses_client_id      ON licenses (client_id);
CREATE INDEX idx_licenses_status         ON licenses (status);
CREATE INDEX idx_licenses_end_date       ON licenses (end_date);
CREATE INDEX idx_licenses_movie_client   ON licenses (movie_id, client_id);

-- Rentals
CREATE INDEX idx_rentals_movie_id   ON rentals (movie_id);
CREATE INDEX idx_rentals_client_id  ON rentals (client_id);
CREATE INDEX idx_rentals_status     ON rentals (status);
