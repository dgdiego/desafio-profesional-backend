DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS accounts;

-- Tabla ACCOUNTS
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DOUBLE NOT NULL
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

-- Tabla TRANSACTIONS
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    date_time TIMESTAMP NOT NULL,
    detail VARCHAR(255),
    type VARCHAR(50) NOT NULL,

    account_id BIGINT,
    card_id BIGINT,
    account_from_id BIGINT,

    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_transactions_card
        FOREIGN KEY (card_id) REFERENCES cards(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_transactions_account_from
        FOREIGN KEY (account_from_id) REFERENCES accounts(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

