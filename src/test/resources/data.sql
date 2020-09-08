INSERT INTO roles(id, role_name)
VALUES (0, 'ROLE_USER');
INSERT INTO roles(id, role_name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO users(id, email, first_name, last_name, verified, created_at, password)
VALUES (100, 'admin@gmail.com', 'admin', 'admin', true, '2020-09-03 06:25:33',
        '$2a$10$u/dS3NMfhKsA3tNT0ffdCOHi/cQEvl6kRimk2vS2IB.T.2cSxpjIa');
INSERT INTO users(id, email, first_name, last_name, verified, created_at, password)
VALUES (102, 'user@gmail.com', 'user', 'user', false, '2020-09-03 06:25:33',
        '$2a$10$u/dS3NMfhKsA3tNT0ffdCOHi/cQEvl6kRimk2vS2IB.T.2cSxpjIa');
INSERT INTO users_roles(user_id, role_id)
VALUES (100, 1);
INSERT INTO password_reset_token(id, expiry_date, token, user_id)
VALUES (100, '2100-09-04 07:55:55', '4824403807', 100);
INSERT INTO password_reset_token(id, expiry_date, token, user_id)
VALUES (101, '2019-09-04 07:55:55', '5456709816', 100);
INSERT INTO verification_token(id, expiry_date, token, user_id)
VALUES (101, '2100-09-04 07:55:55', '5456709816', 102);
INSERT INTO verification_token(id, expiry_date, token, user_id)
VALUES (100, '2019-09-04 07:55:55', '8976452343', 102);
