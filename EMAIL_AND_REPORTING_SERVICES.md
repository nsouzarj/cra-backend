# Serviços de Email e Relatórios

Este documento explica como utilizar os serviços de email e relatórios recém-implementados na aplicação CRA Backend.

## Serviço de Email

O serviço de email permite o envio de emails de texto simples e emails HTML com anexos.

### Configuração

Para utilizar o serviço de email, é necessário configurar as definições de email no arquivo `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Endpoints da API

#### Enviar Email Simples

```
POST /api/email/send
```

Corpo da Requisição:
```json
{
  "to": "destinatario@exemplo.com",
  "subject": "Assunto do Email",
  "text": "Conteúdo do email",
  "isHtml": false
}
```

#### Enviar Email Avançado (com CC e BCC)

```
POST /api/email/send-advanced
```

Corpo da Requisição:
```json
{
  "to": "destinatario@exemplo.com",
  "cc": ["cc1@exemplo.com", "cc2@exemplo.com"],
  "bcc": ["bcc1@exemplo.com"],
  "subject": "Assunto do Email",
  "text": "Conteúdo do email",
  "isHtml": false
}
```

## Serviço de Relatórios

O serviço de relatórios utiliza o JasperReports para gerar relatórios em vários formatos (PDF, HTML, XLS).

### Configuração

Os relatórios devem ser colocados no diretório `src/main/resources/reports/` e compilados para arquivos `.jasper`.

### Endpoints da API

#### Gerar Relatório PDF

```
POST /api/reports/{reportName}/pdf
```

Corpo da Requisição:
```json
{
  "parameters": {
    "param1": "valor1",
    "param2": "valor2"
  },
  "data": [
    {
      "field1": "dado1",
      "field2": "dado2"
    }
  ]
}
```

#### Gerar Relatório em Formato Específico

```
POST /api/reports/{reportName}/{format}
```

Formatos suportados: PDF, HTML, XLS

Corpo da Requisição:
```json
{
  "parameters": {
    "param1": "valor1"
  },
  "data": [
    {
      "field1": "dado1"
    }
  ]
}
```

## Classes de Serviço

### EmailService

Localizado em: `src/main/java/br/adv/cra/service/EmailService.java`

Métodos principais:
- `sendSimpleMessage(String to, String subject, String text)` - Envia um email de texto simples
- `sendMessageWithCCAndBCC(String to, String[] cc, String[] bcc, String subject, String text)` - Envia email com CC/BCC
- `sendHtmlMessage(String to, String subject, String html)` - Envia um email HTML
- `sendHtmlMessageWithAttachments(String to, String subject, String html, File[] attachments)` - Envia email HTML com anexos

### ReportService

Localizado em: `src/main/java/br/adv/cra/service/ReportService.java`

Métodos principais:
- `generatePdfReport(String reportName, Map<String, Object> parameters, Collection<?> data)` - Gera um relatório PDF
- `generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType)` - Gera um relatório em formato especificado

## Classes de Controlador

### EmailController

Localizado em: `src/main/java/br/adv/cra/controller/EmailController.java`

Expõe o serviço de email através de endpoints REST.

### ReportController

Localizado em: `src/main/java/br/adv/cra/controller/ReportController.java`

Expõe o serviço de relatórios através de endpoints REST.

## Classes DTO

### EmailRequest

Localizado em: `src/main/java/br/adv/cra/dto/EmailRequest.java`

Utilizado para payloads de requisições de email.

### ReportRequest

Localizado em: `src/main/java/br/adv/cra/dto/ReportRequest.java`

Utilizado para payloads de requisições de relatórios.