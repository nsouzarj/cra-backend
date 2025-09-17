-- Trigger function to automatically insert a record into historico table
-- when a new solicitacao is created or updated

-- Create the trigger function
CREATE OR REPLACE FUNCTION create_historico_on_solicitacao_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Insert a new record into historico table for both INSERT and UPDATE
    INSERT INTO historico (
        idhistorico,
        datahistorico,
        idsolicitacao,
        idstatus,
        idusuario,
        idrenumeracao
        -- textohistorico is not used as mentioned
    ) VALUES (
        nextval('idhistorico'),   -- idhistorico - use the sequence to generate the ID
        NOW(),                    -- datahistorico - current timestamp
        NEW.idsolicitacao,        -- idsolicitacao - from the solicitacao
        NEW.idstatus,             -- idstatus - from the solicitacao
        NEW.idusuario,            -- idusuario - from the solicitacao
        NEW.idrenumeracao         -- idrenumeracao - from the solicitacao
        -- textohistorico is not used as mentioned, so it's omitted
    );
    
    -- Return the new record to proceed with the operation
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that fires after insert or update on solicitacao table
CREATE TRIGGER solicitacao_change_trigger
    AFTER INSERT OR UPDATE ON solicitacao
    FOR EACH ROW
    EXECUTE FUNCTION create_historico_on_solicitacao_change();

-- To drop the trigger and function if needed:
-- DROP TRIGGER IF EXISTS solicitacao_change_trigger ON solicitacao;
-- DROP FUNCTION IF EXISTS create_historico_on_solicitacao_change();