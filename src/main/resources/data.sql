-- ===================================================================
-- BASIC DATA INITIALIZATION SCRIPT FOR H2 DATABASE (DEV PROFILE)
-- ===================================================================

-- UF data (using MERGE for H2 compatibility)
MERGE INTO uf (iduf, sigla, nome) VALUES (1, 'SP', 'São Paulo');
MERGE INTO uf (iduf, sigla, nome) VALUES (2, 'RJ', 'Rio de Janeiro');
MERGE INTO uf (iduf, sigla, nome) VALUES (3, 'MG', 'Minas Gerais');

-- Endereco data
MERGE INTO endereco (idendereco, logradouro, numero, complemento, bairro, cidade, uf_id, cep, observacao) VALUES (1, 'Rua das Flores', '123', 'Sala 101', 'Centro', 'São Paulo', 1, '01001-000', 'Endereço principal');
MERGE INTO endereco (idendereco, logradouro, numero, complemento, bairro, cidade, uf_id, cep, observacao) VALUES (2, 'Avenida Brasil', '456', 'Sala 202', 'Jardins', 'São Paulo', 1, '01401-000', 'Endereço secundário');

-- Órgãos data (using MERGE for H2 compatibility)
MERGE INTO orgao (idorgao, descricao) VALUES (1, 'Tribunal de Justiça');
MERGE INTO orgao (idorgao, descricao) VALUES (2, 'Tribunal Regional Federal');
MERGE INTO orgao (idorgao, descricao) VALUES (3, 'Superior Tribunal de Justiça');
MERGE INTO orgao (idorgao, descricao) VALUES (4, 'Tribunal Superior do Trabalho');
MERGE INTO orgao (idorgao, descricao) VALUES (5, 'Tribunal Regional do Trabalho');

-- Status de Solicitação data
MERGE INTO statussolicitacao (idstatus, status) VALUES (1, 'Pendente');
MERGE INTO statussolicitacao (idstatus, status) VALUES (2, 'Em Andamento');
MERGE INTO statussolicitacao (idstatus, status) VALUES (3, 'Concluída');
MERGE INTO statussolicitacao (idstatus, status) VALUES (4, 'Cancelada');

-- Tipo de Solicitação data
MERGE INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES (1, 'Protesto', 'Solicitação de protesto de títulos', 'Protesto', true);
MERGE INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES (2, 'Cobrança', 'Solicitação de cobrança extrajudicial', 'Cobrança', true);
MERGE INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES (3, 'Notificação', 'Solicitação de notificação extrajudicial', 'Notificação', true);
MERGE INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES (4, 'Audiência', 'Solicitação de agendamento de audiência', 'Audiência', true);
MERGE INTO tiposolicitacao (idtiposolicitacao, especie, descricao, tipo, visualizar) VALUES (5, 'Diligência', 'Solicitação de diligência extrajudicial', 'Diligência', true);

-- Some basic users (passwords are encrypted with BCrypt)
-- admin/admin123 -> $2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga
-- advogado1/adv123 -> $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
-- corresp1/corresp123 -> $2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu
-- isomina/isomina123 -> $2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG

MERGE INTO usuario (idusuario, login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES (1, 'admin', '$2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga', 'Administrador do Sistema', 'admin@cra.com.br', 1, CURRENT_TIMESTAMP, true);
MERGE INTO usuario (idusuario, login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES (2, 'advogado1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Dr. João Silva', 'joao.silva@cra.com.br', 2, CURRENT_TIMESTAMP, true);
MERGE INTO usuario (idusuario, login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES (3, 'corresp1', '$2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu', 'Maria Santos', 'maria.santos@correspondente.com.br', 3, CURRENT_TIMESTAMP, true);
MERGE INTO usuario (idusuario, login, senha, nomecompleto, emailprincipal, tipo, dataentrada, ativo) VALUES (4, 'isomina', '$2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG', 'Isomina', 'isomina@cra.com.br', 2, CURRENT_TIMESTAMP, true);

-- Some basic correspondente data
MERGE INTO correspondente (idcorrespondente, nome, responsavel, cpfcnpj, oab, tipocorrepondente, telefoneprimario, telefonesecundario, telefonecelularprimario, telefonecelularsecundario, emailprimario, emailsecundario, datacadastro, ativo, observacao, enderecos_id, aplicaregra1, aplicaregra2) VALUES (1, 'Correspondente Legal Ltda', 'Carlos Silva', '12345678901234', '123456SP', 'Advogado', '(11) 1111-1111', '(11) 2222-2222', '(11) 99999-9999', '(11) 88888-8888', 'contato@correspondentelegal.com.br', 'financeiro@correspondentelegal.com.br', CURRENT_TIMESTAMP, true, 'Correspondente principal', 1, true, false);
MERGE INTO correspondente (idcorrespondente, nome, responsavel, cpfcnpj, oab, tipocorrepondente, telefoneprimario, telefonesecundario, telefonecelularprimario, telefonecelularsecundario, emailprimario, emailsecundario, datacadastro, ativo, observacao, enderecos_id, aplicaregra1, aplicaregra2) VALUES (2, 'Advocacia Associada ME', 'Ana Costa', '98765432109876', '654321SP', 'Estagiário', '(11) 3333-3333', '(11) 4444-4444', '(11) 77777-7777', '(11) 66666-6666', 'contato@advocaciaassociada.com.br', 'rh@advocaciaassociada.com.br', CURRENT_TIMESTAMP, true, 'Correspondente secundário', 2, false, true);

-- Some basic processo data (without comarca_id since it will be loaded separately)
MERGE INTO processo (idprocesso, numeroprocesso, numeroprocessopesq, parte, adverso, posicao, status, cartorio, assunto, localizacao, numerointegracao, orgao_idorgao, numorgao, proceletronico, quantsoli) VALUES (1, '1234567-89.2023.8.26.0001', '12345678920238260001', 'Empresa Alpha Ltda', 'Fulano de Tal', 'Ré', 'Em andamento', '1º Cartório de Protesto', 'Cobrança de duplicata', 'Sala 101', 'INT-001', 1, 1, 'S', 5);
MERGE INTO processo (idprocesso, numeroprocesso, numeroprocessopesq, parte, adverso, posicao, status, cartorio, assunto, localizacao, numerointegracao, orgao_idorgao, numorgao, proceletronico, quantsoli) VALUES (2, '9876543-21.2023.8.26.0002', '98765432120238260002', 'José da Silva', 'Empresa Beta S.A.', 'Autor', 'Arquivado', '2º Cartório de Protesto', 'Execução fiscal', 'Sala 102', 'INT-002', 2, 2, 'N', 3);

-- Some basic solicitacao data (without idcomarca since it will be loaded separately)
MERGE INTO solicitacao (idsolicitacao, referenciasolicitacao, datasolictacao, dataprazo, observacao, instrucoes, complemento, justificativa, tratposaudiencia, numcontrole, tempreposto, convolada, horaudiencia, statusexterno, processo_id, idusuario, valor, valordaalcada, emailenvio, pago, grupo, propostaacordo, audinterna, lide, avaliacaonota, textoavaliacao, idstatus) VALUES (1, 1001, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'Solicitação de protesto', 'Protocolar no cartório', 'Documento anexo', 'Débito em atraso', 'Enviar após audiência', 'CTRL-001', false, true, '10:00', 'CONFIRMAR', 1, 2, 500.00, 1000.00, 'contato@cliente.com.br', 'false', 1, false, true, 'S', 5, 'Atendimento excelente', 1);
MERGE INTO solicitacao (idsolicitacao, referenciasolicitacao, datasolictacao, dataprazo, observacao, instrucoes, complemento, justificativa, tratposaudiencia, numcontrole, tempreposto, convolada, horaudiencia, statusexterno, processo_id, idusuario, valor, valordaalcada, emailenvio, pago, grupo, propostaacordo, audinterna, lide, avaliacaonota, textoavaliacao, idstatus) VALUES (2, 1002, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15' DAY, 'Solicitação de cobrança', 'Enviar notificação extrajudicial', 'Contrato anexo', 'Inadimplemento contratual', 'Tratar após reunião', 'CTRL-002', true, false, '14:30', 'REJEITAR', 2, 3, 1200.50, 2000.00, 'financeiro@cliente.com.br', 'true', 2, true, false, 'N', 4, 'Bom serviço', 2);