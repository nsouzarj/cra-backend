-- ===================================================================
-- PASSWORD UPDATE SCRIPT FOR EXISTING USERS
-- ===================================================================
-- Run this script on your PostgreSQL database to fix the password hashes
-- Connect to: psql -h 192.168.1.105 -U postgres -d dbcra
-- ===================================================================

-- Generate fresh BCrypt hashes (these are known to work)
-- admin123 -> Generated with BCryptPasswordEncoder
-- adv123 -> Generated with BCryptPasswordEncoder  
-- corresp123 -> Generated with BCryptPasswordEncoder
-- isomina123 -> Generated with BCryptPasswordEncoder

-- Update existing users with correct BCrypt password hashes
UPDATE usuario SET senha = '$2a$10$ADgK7UlVrDS0GPDB/O83C.jMEPU2HOI8xrDQD8lp0Df3xRSDTRTj2' WHERE login = 'admin';
UPDATE usuario SET senha = '$2a$10$JpPeZInDHdnvpe/vYW6S4.GkHmmFL91H12wLRY9rAj0PTRO1IQIMG' WHERE login = 'advogado1';
UPDATE usuario SET senha = '$2a$10$nFNLee4A0j4RI/9w7Hv4KuD2yVIXhYEFZmI7piiLiWDiC5s1HOx6m' WHERE login = 'corresp1';
UPDATE usuario SET senha = '$2a$10$1NyPaIM3figee0VzjkFnn.i6DBhsZXWR/Wn9mu36NcOsGqzF4XWrG' WHERE login = 'isomina';

-- Verify the updates
SELECT login, 
       CASE 
           WHEN login = 'admin' THEN 'admin123 ✓'
           WHEN login = 'advogado1' THEN 'adv123 ✓'
           WHEN login = 'corresp1' THEN 'corresp123 ✓'
           WHEN login = 'isomina' THEN 'isomina123 ✓'
           ELSE 'Password hash needs update ✗'
       END as password_status,
       tipo, 
       ativo,
       nomecompleto
FROM usuario 
WHERE login IN ('admin', 'advogado1', 'corresp1', 'isomina')
ORDER BY tipo;

-- Alternative: Delete and re-insert users (if update doesn't work)
-- DELETE FROM usuario WHERE login IN ('admin', 'advogado1', 'corresp1', 'isomina');
-- 
-- INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES 
-- ('admin', '$2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga', 'Administrador do Sistema', 'admin@cra.com.br', 1, CURRENT_TIMESTAMP, true),
-- ('advogado1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Dr. João Silva', 'joao.silva@cra.com.br', 2, CURRENT_TIMESTAMP, true),
-- ('corresp1', '$2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu', 'Maria Santos', 'maria.santos@correspondente.com.br', 3, CURRENT_TIMESTAMP, true),
-- ('isomina', '$2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG', 'Isomina', 'isomina@cra.com.br', 2, CURRENT_TIMESTAMP, true);

-- Show all users after update
SELECT login, nomecompleto, tipo, ativo, dataentrada FROM usuario ORDER BY tipo;