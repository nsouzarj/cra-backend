package br.adv.cra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@adv.cra.br}") // Default value if not set
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple email message asynchronously.
     *
     * @param to      Recipient email address
     * @param subject Email subject
     * @param text    Email body content
     */
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Simple email sent successfully to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send simple email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Sends an email message with CC and BCC recipients asynchronously.
     *
     * @param to      Recipient email address
     * @param cc      CC recipient email addresses
     * @param bcc     BCC recipient email addresses
     * @param subject Email subject
     * @param text    Email body content
     */
    @Async
    public void sendMessageWithCCAndBCC(String to, String subject, String text, String[] cc, String... bcc) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setCc(cc);
            if (bcc != null && bcc.length > 0) {
                message.setBcc(bcc);
            }
            mailSender.send(message);
            logger.info("Email with CC/BCC sent successfully to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email with CC/BCC to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Sends an HTML email message asynchronously.
     *
     * @param to      Recipient email address
     * @param subject Email subject
     * @param html    HTML content
     */
    @Async
    public void sendHtmlMessage(String to, String subject, String html) {
        try {
            MimeMessage message = createMimeMessage(to, subject, html);
            mailSender.send(message);
            logger.info("HTML email sent successfully to {}", to);
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Sends an HTML email with attachments asynchronously.
     *
     * @param to           Recipient email address
     * @param subject      Email subject
     * @param html         HTML content
     * @param attachments  File attachments
     */
    @Async
    public void sendHtmlMessageWithAttachments(String to, String subject, String html, File... attachments) {
        try {
            MimeMessage message = createMimeMessage(to, subject, html, attachments);
            mailSender.send(message);
            logger.info("HTML email with attachments sent successfully to {}", to);
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send HTML email with attachments to {}: {}", to, e.getMessage());
        }
    }

    private MimeMessage createMimeMessage(String to, String subject, String html, File... attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indicates HTML content
        if (attachments != null) {
            for (File attachment : attachments) {
                helper.addAttachment(attachment.getName(), attachment);
            }
        }
        return message;
    }
}