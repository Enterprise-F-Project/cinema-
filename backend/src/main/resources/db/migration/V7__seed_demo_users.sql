-- Demo accounts for development and testing (password: Password123!)
INSERT INTO users (full_name, email, password, role, active)
VALUES
    ('Platform Admin', 'admin@cinema.com', '$2a$12$hhfA0gkhZqarCc74tSGDuuh1sR35LDwG.udK2EaPxF1fXnV9dGXO.', 'ADMIN', TRUE),
    ('Movie Distributor', 'distributor@cinema.com', '$2a$12$hhfA0gkhZqarCc74tSGDuuh1sR35LDwG.udK2EaPxF1fXnV9dGXO.', 'DISTRIBUTOR', TRUE),
    ('Cinema Client', 'client@cinema.com', '$2a$12$hhfA0gkhZqarCc74tSGDuuh1sR35LDwG.udK2EaPxF1fXnV9dGXO.', 'CLIENT', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO clients (organization_name, email, phone, address)
VALUES ('Demo Cinema', 'client@cinema.com', '+251900000000', 'Addis Ababa, Ethiopia')
ON CONFLICT (email) DO NOTHING;
