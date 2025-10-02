package br.adv.cra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService();
        
        // Use reflection to inject the mock since EmailService uses @Autowired
        try {
            java.lang.reflect.Field mailSenderField = EmailService.class.getDeclaredField("mailSender");
            mailSenderField.setAccessible(true);
            mailSenderField.set(emailService, mailSender);
        } catch (Exception e) {
            fail("Failed to inject mailSender via reflection: " + e.getMessage());
        }
    }

    @Test
    void testSendSimpleMessage_ShouldSendEmail() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendSimpleMessage(to, subject, text);

        // Then
        // Verify that the mail sender was called with a SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendMessageWithCCAndBCC_ShouldSendEmailWithCCAndBCC() {
        // Given
        String to = "test@example.com";
        String[] cc = {"cc@example.com"};
        String[] bcc = {"bcc@example.com"};
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendMessageWithCCAndBCC(to, cc, bcc, subject, text);

        // Then
        // Verify that the mail sender was called with a SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendMessageWithCCAndBCC_NullCCAndBCC_ShouldSendEmail() {
        // Given
        String to = "test@example.com";
        String[] cc = null;
        String[] bcc = null;
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendMessageWithCCAndBCC(to, cc, bcc, subject, text);

        // Then
        // Verify that the mail sender was called with a SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendMessageWithCCAndBCC_EmptyCCAndBCC_ShouldSendEmail() {
        // Given
        String to = "test@example.com";
        String[] cc = {};
        String[] bcc = {};
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendMessageWithCCAndBCC(to, cc, bcc, subject, text);

        // Then
        // Verify that the mail sender was called with a SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendHtmlMessage_ShouldSendHtmlEmail() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        // When
        emailService.sendHtmlMessage(to, subject, html);

        // Then
        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendHtmlMessage_MessagingException_ShouldThrowException() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(mimeMessage);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendHtmlMessage(to, subject, html);
        });

        assertEquals("Failed to send email", exception.getMessage());

        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendHtmlMessageWithAttachments_ShouldSendHtmlEmailWithAttachments() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";
        File[] attachments = {new File("test.txt")};

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        // When
        emailService.sendHtmlMessageWithAttachments(to, subject, html, attachments);

        // Then
        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendHtmlMessageWithAttachments_NullAttachments_ShouldSendHtmlEmail() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";
        File[] attachments = null;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        // When
        emailService.sendHtmlMessageWithAttachments(to, subject, html, attachments);

        // Then
        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendHtmlMessageWithAttachments_EmptyAttachments_ShouldSendHtmlEmail() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";
        File[] attachments = {};

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        // When
        emailService.sendHtmlMessageWithAttachments(to, subject, html, attachments);

        // Then
        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendHtmlMessageWithAttachments_MessagingException_ShouldThrowException() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String html = "<h1>Test HTML Message</h1>";
        File[] attachments = {new File("test.txt")};

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(mimeMessage);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendHtmlMessageWithAttachments(to, subject, html, attachments);
        });

        assertEquals("Failed to send email", exception.getMessage());

        // Verify that the mail sender was called
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }
}