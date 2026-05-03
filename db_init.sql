
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE otp_config (
    id SERIAL PRIMARY KEY,
    code_length INT NOT NULL,
    lifetime_seconds INT NOT NULL,
    CONSTRAINT single_row_constraint UNIQUE (id)
);


CREATE TABLE otp_codes (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    operation_id VARCHAR(255),
    code VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


INSERT INTO otp_config (id, code_length, lifetime_seconds) VALUES (1, 6, 300);
