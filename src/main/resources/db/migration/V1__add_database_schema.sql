CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email_address VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    municipality_id VARCHAR(10),
    status VARCHAR(20),
    password VARCHAR(255)
);
