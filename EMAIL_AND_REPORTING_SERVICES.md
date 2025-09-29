# Email and Reporting Services

This document explains how to use the newly implemented email and reporting services in the CRA Backend application.

## Email Service

The email service allows sending simple text emails and HTML emails with attachments.

### Configuration

To use the email service, you need to configure the email settings in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### API Endpoints

#### Send Simple Email

```
POST /api/email/send
```

Request Body:
```json
{
  "to": "recipient@example.com",
  "subject": "Email Subject",
  "text": "Email content",
  "isHtml": false
}
```

#### Send Advanced Email (with CC and BCC)

```
POST /api/email/send-advanced
```

Request Body:
```json
{
  "to": "recipient@example.com",
  "cc": ["cc1@example.com", "cc2@example.com"],
  "bcc": ["bcc1@example.com"],
  "subject": "Email Subject",
  "text": "Email content",
  "isHtml": false
}
```

## Reporting Service

The reporting service uses JasperReports to generate reports in various formats (PDF, HTML, XLS).

### Configuration

Reports should be placed in the `src/main/resources/reports/` directory and compiled to `.jasper` files.

### API Endpoints

#### Generate PDF Report

```
POST /api/reports/{reportName}/pdf
```

Request Body:
```json
{
  "parameters": {
    "param1": "value1",
    "param2": "value2"
  },
  "data": [
    {
      "field1": "data1",
      "field2": "data2"
    }
  ]
}
```

#### Generate Report in Specific Format

```
POST /api/reports/{reportName}/{format}
```

Supported formats: PDF, HTML, XLS

Request Body:
```json
{
  "parameters": {
    "param1": "value1"
  },
  "data": [
    {
      "field1": "data1"
    }
  ]
}
```

## Service Classes

### EmailService

Located at: `src/main/java/br/adv/cra/service/EmailService.java`

Main methods:
- `sendSimpleMessage(String to, String subject, String text)` - Sends a simple text email
- `sendMessageWithCCAndBCC(String to, String[] cc, String[] bcc, String subject, String text)` - Sends email with CC/BCC
- `sendHtmlMessage(String to, String subject, String html)` - Sends an HTML email
- `sendHtmlMessageWithAttachments(String to, String subject, String html, File[] attachments)` - Sends HTML email with attachments

### ReportService

Located at: `src/main/java/br/adv/cra/service/ReportService.java`

Main methods:
- `generatePdfReport(String reportName, Map<String, Object> parameters, Collection<?> data)` - Generates a PDF report
- `generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType)` - Generates a report in specified format

## Controller Classes

### EmailController

Located at: `src/main/java/br/adv/cra/controller/EmailController.java`

Exposes the email service via REST endpoints.

### ReportController

Located at: `src/main/java/br/adv/cra/controller/ReportController.java`

Exposes the reporting service via REST endpoints.

## DTO Classes

### EmailRequest

Located at: `src/main/java/br/adv/cra/dto/EmailRequest.java`

Used for email request payloads.

### ReportRequest

Located at: `src/main/java/br/adv/cra/dto/ReportRequest.java`

Used for report request payloads.# Email and Reporting Services

This document explains how to use the newly implemented email and reporting services in the CRA Backend application.

## Email Service

The email service allows sending simple text emails and HTML emails with attachments.

### Configuration

To use the email service, you need to configure the email settings in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### API Endpoints

#### Send Simple Email

```
POST /api/email/send
```

Request Body:
```json
{
  "to": "recipient@example.com",
  "subject": "Email Subject",
  "text": "Email content",
  "isHtml": false
}
```

#### Send Advanced Email (with CC and BCC)

```
POST /api/email/send-advanced
```

Request Body:
```json
{
  "to": "recipient@example.com",
  "cc": ["cc1@example.com", "cc2@example.com"],
  "bcc": ["bcc1@example.com"],
  "subject": "Email Subject",
  "text": "Email content",
  "isHtml": false
}
```

## Reporting Service

The reporting service uses JasperReports to generate reports in various formats (PDF, HTML, XLS).

### Configuration

Reports should be placed in the `src/main/resources/reports/` directory and compiled to `.jasper` files.

### API Endpoints

#### Generate PDF Report

```
POST /api/reports/{reportName}/pdf
```

Request Body:
```json
{
  "parameters": {
    "param1": "value1",
    "param2": "value2"
  },
  "data": [
    {
      "field1": "data1",
      "field2": "data2"
    }
  ]
}
```

#### Generate Report in Specific Format

```
POST /api/reports/{reportName}/{format}
```

Supported formats: PDF, HTML, XLS

Request Body:
```json
{
  "parameters": {
    "param1": "value1"
  },
  "data": [
    {
      "field1": "data1"
    }
  ]
}
```

## Service Classes

### EmailService

Located at: `src/main/java/br/adv/cra/service/EmailService.java`

Main methods:
- `sendSimpleMessage(String to, String subject, String text)` - Sends a simple text email
- `sendMessageWithCCAndBCC(String to, String[] cc, String[] bcc, String subject, String text)` - Sends email with CC/BCC
- `sendHtmlMessage(String to, String subject, String html)` - Sends an HTML email
- `sendHtmlMessageWithAttachments(String to, String subject, String html, File[] attachments)` - Sends HTML email with attachments

### ReportService

Located at: `src/main/java/br/adv/cra/service/ReportService.java`

Main methods:
- `generatePdfReport(String reportName, Map<String, Object> parameters, Collection<?> data)` - Generates a PDF report
- `generateReport(String reportName, Map<String, Object> parameters, Collection<?> data, String outputType)` - Generates a report in specified format

## Controller Classes

### EmailController

Located at: `src/main/java/br/adv/cra/controller/EmailController.java`

Exposes the email service via REST endpoints.

### ReportController

Located at: `src/main/java/br/adv/cra/controller/ReportController.java`

Exposes the reporting service via REST endpoints.

## DTO Classes

### EmailRequest

Located at: `src/main/java/br/adv/cra/dto/EmailRequest.java`

Used for email request payloads.

### ReportRequest

Located at: `src/main/java/br/adv/cra/dto/ReportRequest.java`

Used for report request payloads.