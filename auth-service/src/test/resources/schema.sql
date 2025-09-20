create table expired_tokens (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     expiration_date TIMESTAMP,
     token VARCHAR(255)
);
