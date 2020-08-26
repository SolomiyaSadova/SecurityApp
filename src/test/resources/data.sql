INSERT INTO roles(id, role_name)
VALUES (0, 'ROLE_USER');
INSERT INTO roles(id, role_name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO users(id, email, first_name, last_name, password)
VALUES (100, 'admin@gmail.com', 'admin', 'admin', '$2a$10$u/dS3NMfhKsA3tNT0ffdCOHi/cQEvl6kRimk2vS2IB.T.2cSxpjIa');
INSERT INTO users_roles(user_id, role_id)
VALUES (100, 1);

