create table roles (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    dni VARCHAR(20) NOT NULL,
    phone VARCHAR(50)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, rol_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);
