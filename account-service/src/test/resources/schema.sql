DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS accounts;

-- Tabla ACCOUNTS
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DOUBLE NOT NULL
);

-- Tabla TRANSACTIONS
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    date_time TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    account_id BIGINT,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Tabla CARDS
CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    expiration_date DATE NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

