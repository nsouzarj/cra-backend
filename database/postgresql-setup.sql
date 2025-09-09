-- ===================================================================
-- POSTGRESQL DATABASE SCHEMA SETUP FOR CRA PROJECT
-- ===================================================================
-- Server: 192.168.1.105:5432
-- Database: dbcra
-- User: postgres
-- Password: nso1810
-- ===================================================================

-- Connect to PostgreSQL and create database (run as postgres user)
-- psql -h 192.168.1.105 -U postgres -c "CREATE DATABASE dbcra WITH ENCODING 'UTF8';"

-- Connect to the dbcra database
-- \c dbcra

-- Verify connection
SELECT version();
SELECT current_database();
SELECT current_user;

-- Show existing tables (if any)
\dt

-- The application will automatically create tables using Hibernate DDL
-- Based on the entity classes when spring.jpa.hibernate.ddl-auto=update

-- Expected tables that will be created:
-- - uf
-- - endereco  
-- - comarca
-- - orgao
-- - correspondente
-- - usuario
-- - processo
-- - solicitacao

-- After running the application, you can verify tables with:
-- \dt
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

-- Check table structures:
-- \d uf
-- \d usuario
-- \d correspondente
-- \d processo
-- \d solicitacao

-- Verify initial data:
-- SELECT * FROM uf;
-- SELECT login, nomecompleto, tipo FROM usuario;

-- Grant permissions if needed (usually not required for owner)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;