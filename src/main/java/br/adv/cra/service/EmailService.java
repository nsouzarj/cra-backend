package br.adv.cra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired

    private JavaMailSender mailSender;

    /**
     * Sends a simple email message
     * 
     * @param to      Recipient email address
     * @param subject Email subject
     * @param text    Email body content
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Sends an email message with CC and BCC recipients
     * 
     * @param to      Recipient email address
     * @param cc      CC recipient email addresses
     * @param bcc     BCC recipient email addresses
     * @param subject Email subject
     * @param text    Email body content
     */
    public void sendMessageWithCCAndBCC(String to, String[] cc, String[] bcc, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        if (cc != null && cc.length > 0) {
            message.setCc(cc);
        }
        if (bcc != null && bcc.length > 0) {
            message.setBcc(bcc);
        }
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Sends an HTML email message
     * 
     * @param to      Recipient email address
     * @param subject Email subject
     * @param html    HTML content
     * @throws MessagingException If there's an error creating or sending the message
     */
    public void sendHtmlMessage(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indicates HTML content
        mailSender.send(message);
    }

    /**
     * Sends an HTML email with attachments
     * 
     * @param to           Recipient email address
     * @param subject      Email subject
     * @param html         HTML content
     * @param attachments  File attachments
     * @throws MessagingException If there's an error creating or sending the message
     */
    public void sendHtmlMessageWithAttachments(String to, String subject, String html, 
                                              java.io.File[] attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true indicates HTML content
        
        if (attachments != null) {
            for (java.io.File attachment : attachments) {
                helper.addAttachment(attachment.getName(), attachment);
            }
        }
        
        mailSender.send(message);
    }
}