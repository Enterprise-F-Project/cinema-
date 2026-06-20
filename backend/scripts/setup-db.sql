CREATE USER cinema_user WITH PASSWORD 'cinema_pass';
CREATE DATABASE cinema_db OWNER cinema_user;
GRANT ALL PRIVILEGES ON DATABASE cinema_db TO cinema_user;
