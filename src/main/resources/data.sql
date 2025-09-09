-- ===================================================================
-- BASIC DATA INITIALIZATION SCRIPT FOR POSTGRESQL
-- ===================================================================

-- Órgãos data
INSERT INTO orgao (descricao) VALUES ('Tribunal de Justiça') ON CONFLICT DO NOTHING;
INSERT INTO orgao (descricao) VALUES ('Tribunal Regional Federal') ON CONFLICT DO NOTHING;
INSERT INTO orgao (descricao) VALUES ('Superior Tribunal de Justiça') ON CONFLICT DO NOTHING;
INSERT INTO orgao (descricao) VALUES ('Tribunal Superior do Trabalho') ON CONFLICT DO NOTHING;
INSERT INTO orgao (descricao) VALUES ('Tribunal Regional do Trabalho') ON CONFLICT DO NOTHING;

-- Status de Solicitação data
INSERT INTO statussolicitacao (idstatus, status) VALUES 
(1, 'Pendente'),
(2, 'Em Andamento'),
(3, 'Concluída'),
(4, 'Cancelada')
ON CONFLICT DO NOTHING;

-- Tipo de Solicitação data
INSERT INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES 
(1, 'Protesto', 'Solicitação de protesto de títulos', 'Protesto', true),
(2, 'Cobrança', 'Solicitação de cobrança extrajudicial', 'Cobrança', true),
(3, 'Notificação', 'Solicitação de notificação extrajudicial', 'Notificação', true),
(4, 'Audiência', 'Solicitação de agendamento de audiência', 'Audiência', true),
(5, 'Diligência', 'Solicitação de diligência extrajudicial', 'Diligência', true);

-- Some basic users (passwords are encrypted with BCrypt)
-- admin/admin123 -> $2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga
-- advogado1/adv123 -> $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
-- corresp1/corresp123 -> $2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu
-- isomina/isomina123 -> $2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG

INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES ('admin', '$2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga', 'Administrador do Sistema', 'admin@cra.com.br', 1, CURRENT_TIMESTAMP, true);
INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES ('advogado1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Dr. João Silva', 'joao.silva@cra.com.br', 2, CURRENT_TIMESTAMP, true);
INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES ('corresp1', '$2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu', 'Maria Santos', 'maria.santos@correspondente.com.br', 3, CURRENT_TIMESTAMP, true);
INSERT INTO usuario (login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES ('isomina', '$2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG', 'Isomina', 'isomina@cra.com.br', 2, CURRENT_TIMESTAMP, true)
ON CONFLICT (login) DO NOTHING;

-- Some basic correspondente data
INSERT INTO correspondente (nome, responsavel, cpfcnpj, oab, tipocorrepondente, telefoneprimario, telefonesecundario, telefonecelularprimario, telefonecelularsecundario, emailprimario, emailsecundario, datacadastro, ativo, observacao, enderecos_id, aplicaregra1, aplicaregra2) VALUES
('Correspondente Legal Ltda', 'Carlos Silva', '12345678901234', '123456SP', 'Advogado', '(11) 1111-1111', '(11) 2222-2222', '(11) 99999-9999', '(11) 88888-8888', 'contato@correspondentelegal.com.br', 'financeiro@correspondentelegal.com.br', CURRENT_TIMESTAMP, true, 'Correspondente principal', 1, true, false),
('Advocacia Associada ME', 'Ana Costa', '98765432109876', '654321SP', 'Estagiário', '(11) 3333-3333', '(11) 4444-4444', '(11) 77777-7777', '(11) 66666-6666', 'contato@advocaciaassociada.com.br', 'rh@advocaciaassociada.com.br', CURRENT_TIMESTAMP, true, 'Correspondente secundário', 2, false, true)
ON CONFLICT DO NOTHING;

-- Some basic processo data (without comarca_id since it will be loaded separately)
INSERT INTO processo (numeroprocesso, numeroprocessopesq, parte, adverso, posicao, status, cartorio, assunto, localizacao, numerointegracao, orgao_id, numorgao, proceletronico, quantsoli) VALUES
('1234567-89.2023.8.26.0001', '12345678920238260001', 'Empresa Alpha Ltda', 'Fulano de Tal', 'Ré', 'Em andamento', '1º Cartório de Protesto', 'Cobrança de duplicata', 'Sala 101', 'INT-001', 1, 1, 'S', 5),
('9876543-21.2023.8.26.0002', '98765432120238260002', 'José da Silva', 'Empresa Beta S.A.', 'Autor', 'Arquivado', '2º Cartório de Protesto', 'Execução fiscal', 'Sala 102', 'INT-002', 2, 2, 'N', 3)
ON CONFLICT DO NOTHING;

-- Some basic solicitacao data (without idcomarca since it will be loaded separately)
INSERT INTO solicitacao (referenciasolicitacao, datasolictacao, dataprazo, observacao, instrucoes, complemento, justificativa, tratposaudiencia, numcontrole, tempreposto, convolada, horaudiencia, statusexterno, processo_id, idusuario, valor, valordaalcada, emailenvio, pago, grupo, propostaacordo, audinterna, lide, avaliacaonota, textoavaliacao, idstatus) VALUES
(1001, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'Solicitação de protesto', 'Protocolar no cartório', 'Documento anexo', 'Débito em atraso', 'Enviar após audiência', 'CTRL-001', false, true, '10:00', 'CONFIRMAR', 1, 2, 500.00, 1000.00, 'contato@cliente.com.br', 'false', 1, false, true, 'S', 5, 'Atendimento excelente', 1),
(1002, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 days', 'Solicitação de cobrança', 'Enviar notificação extrajudicial', 'Contrato anexo', 'Inadimplemento contratual', 'Tratar após reunião', 'CTRL-002', true, false, '14:30', 'REJEITAR', 2, 3, 1200.50, 2000.00, 'financeiro@cliente.com.br', 'true', 2, true, false, 'N', 4, 'Bom serviço', 2)
ON CONFLICT DO NOTHING;