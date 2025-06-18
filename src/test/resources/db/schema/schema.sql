CREATE TABLE users (
    party_id VARCHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    email_address VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    municipality_id VARCHAR(10),
    status VARCHAR(20)
);