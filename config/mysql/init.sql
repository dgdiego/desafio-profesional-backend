ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS dgmoney_users_service_db;
CREATE DATABASE IF NOT EXISTS dgmoney_auth_service_db;
CREATE DATABASE IF NOT EXISTS dgmoney_accounts_service_db;
