CREATE DATABASE sensorapi;

-- Switch to the newly created database
\c sensorapi;

-- Create a user table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ADMIN user
INSERT INTO users (username, password) VALUES ('admin', '$2b$10$1GlRpbSn1QH4ZF/TS2i1LuV.okITA4pcjDVE8x6sAncpO.3CmR3x.');


CREATE TABLE token_blacklist (
    token VARCHAR(255) PRIMARY KEY,
    expiration_time BIGINT NOT NULL
);
