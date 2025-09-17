-- Script to add CASCADE DELETE constraint to foreign keys in historico table

-- First, we need to drop the existing foreign key constraints and recreate them with CASCADE DELETE
-- Note: The exact constraint names may vary in your database. 
-- You can find them by running: \d historico in psql

-- Drop existing foreign key constraints (replace with actual constraint names from your database)
ALTER TABLE historico DROP CONSTRAINT IF EXISTS fk_historico_solicitacao;
ALTER TABLE historico DROP CONSTRAINT IF EXISTS fk_historico_status;
ALTER TABLE historico DROP CONSTRAINT IF EXISTS fk_historico_usuario;
ALTER TABLE historico DROP CONSTRAINT IF EXISTS fk_historico_renumeracao;

-- Recreate foreign key constraints with CASCADE DELETE for solicitacao
ALTER TABLE historico 
ADD CONSTRAINT fk_historico_solicitacao 
FOREIGN KEY (idsolicitacao) 
REFERENCES solicitacao(idsolicitacao) 
ON DELETE CASCADE;

-- Recreate foreign key constraints with CASCADE DELETE for status
ALTER TABLE historico 
ADD CONSTRAINT fk_historico_status 
FOREIGN KEY (idstatus) 
REFERENCES statussolicitacao(idstatus) 
ON DELETE CASCADE;

-- Recreate foreign key constraints with CASCADE DELETE for usuario
ALTER TABLE historico 
ADD CONSTRAINT fk_historico_usuario 
FOREIGN KEY (idusuario) 
REFERENCES usuario(idusuario) 
ON DELETE CASCADE;

-- Recreate foreign key constraints with CASCADE DELETE for renumeracao
ALTER TABLE historico 
ADD CONSTRAINT fk_historico_renumeracao 
FOREIGN KEY (idrenumeracao) 
REFERENCES renumeracao(idrenumeracao) 
ON DELETE CASCADE;

-- To verify the constraints were added correctly, you can run:
-- \d historico

-- Alternative approach if you only want CASCADE DELETE on the solicitacao foreign key:
-- ALTER TABLE historico DROP CONSTRAINT IF EXISTS fk_historico_solicitacao;
-- ALTER TABLE historico 
-- ADD CONSTRAINT fk_historico_solicitacao 
-- FOREIGN KEY (idsolicitacao) 
-- REFERENCES solicitacao(idsolicitacao) 
-- ON DELETE CASCADE;