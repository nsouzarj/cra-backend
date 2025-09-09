-- ===================================================================
-- ADD NEW USER: isomina
-- ===================================================================
-- Run this script on your PostgreSQL database to add the user 'isomina'
-- Connect to: psql -h 192.168.1.105 -U postgres -d dbcra
-- ===================================================================

-- Add new user 'isomina' with password 'isomina123'
-- BCrypt hash for 'isomina123': $2a$10$TKh7sJJRs5XyOuqU3pVnJOXZoV5YnxJKlj6yQnGpX1KqU9nX5CkoS
INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES 
('isomina', '$2a$10$TKh7sJJRs5XyOuqU3pVnJOXZoV5YnxJKlj6yQnGpX1KqU9nX5CkoS', 'Isomina', 'isomina@cra.com.br', 2, CURRENT_TIMESTAMP, true)
ON CONFLICT (login) DO UPDATE SET
    senha = EXCLUDED.senha,
    nomecompleto = EXCLUDED.nomecompleto,
    emailprincipal = EXCLUDED.emailprincipal,
    tipo = EXCLUDED.tipo,
    ativo = EXCLUDED.ativo;

-- Verify the user was created
SELECT login, nomecompleto, emailprincipal, tipo, ativo, dataentrada 
FROM usuario 
WHERE login = 'isomina';

-- Show user type mapping
SELECT login, nomecompleto, tipo,
       CASE tipo
           WHEN 1 THEN 'ADMIN'
           WHEN 2 THEN 'ADVOGADO'
           WHEN 3 THEN 'CORRESPONDENTE'
           ELSE 'UNKNOWN'
       END as role
FROM usuario 
WHERE login = 'isomina';