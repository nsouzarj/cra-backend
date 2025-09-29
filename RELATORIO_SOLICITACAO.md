# Relatório de Solicitação

Este documento explica como gerar um relatório PDF com os detalhes de uma solicitação específica.

## Novo Endpoint

Foi adicionado um novo endpoint para gerar relatórios de solicitações:

```
GET /api/reports/solicitacao/{solicitacaoId}
```

### Parâmetros

- `solicitacaoId` (path): O ID da solicitação para a qual gerar o relatório

### Requisitos de Segurança

Este endpoint requer autenticação e autorização. O usuário deve ter uma das seguintes roles:
- ADMIN
- ADVOGADO
- CORRESPONDENTE

O token JWT deve ser incluído no header da requisição:
```
Authorization: Bearer <seu_token_aqui>
```

### Resposta

- **Sucesso**: Retorna um PDF com os detalhes da solicitação
- **Erro 401**: Se o usuário não estiver autenticado
- **Erro 403**: Se o usuário não tiver permissão adequada
- **Erro 404**: Se a solicitação não for encontrada
- **Erro 500**: Se houver um erro ao gerar o relatório

### Exemplo de Uso

Para gerar um relatório para a solicitação com ID 123:

```
GET /api/reports/solicitacao/123
Authorization: Bearer seu_token_jwt_aqui
```

Isso retornará um arquivo PDF chamado `solicitacao-123.pdf` com todos os detalhes da solicitação.

## Funcionalidade de Compilação Automática

O serviço de relatórios agora suporta compilação automática de templates [.jrxml](file://d:\Projetos\craweb\cra-backend\src\main\resources\reports\sample-report.jrxml) para [.jasper](file://d:\Projetos\craweb\cra-backend\target\classes\reports\sample-report.jasper). Isso significa que você pode modificar os templates [.jrxml](file://d:\Projetos\craweb\cra-backend\src\main\resources\reports\sample-report.jrxml) e o sistema irá automaticamente compilá-los na hora da execução, sem necessidade de compilação manual.

## Layout Otimizado

O template de relatório foi otimizado para evitar problemas de layout onde o conteúdo não cabia na página. Agora o relatório utiliza uma estrutura tabular com campos e valores organizados em bandas menores, garantindo que todo o conteúdo caiba adequadamente na página.

## Outros Endpoints de Relatórios

Além do endpoint específico para solicitações, estão disponíveis os seguintes endpoints genéricos:

### Gerar Relatório PDF

```
POST /api/reports/{reportName}/pdf
```

### Gerar Relatório em Formato Específico

```
POST /api/reports/{reportName}/{format}
```

Formatos suportados: PDF, HTML, XLS

## Campos Incluídos no Relatório

O relatório inclui todos os campos relevantes da solicitação:

1. ID
2. Número
3. Datas (solicitação, conclusão, agendamento, prazo)
4. Vara e UF
5. Requerente e requerido
6. Observações, instruções, complemento e justificativa
7. Informações de tratamento pós audiência
8. Número de controle
9. Flags (tempreposto, convolada, propostaacordo, audinterna)
10. Hora da audiência e status externo
11. Valores (valor, valordaalcada)
12. Informações de envio e pagamento
13. Grupo
14. Lide
15. Avaliação (nota e texto)
16. Informações relacionadas (comarca, processo, status, tipo de solicitação, correspondente, usuário)

## Template do Relatório

O template do relatório está localizado em:
`src/main/resources/reports/solicitacao-report.jrxml`

Este é um template JasperReports que pode ser modificado conforme necessário para alterar o layout do relatório. O sistema irá automaticamente compilar este template quando necessário.