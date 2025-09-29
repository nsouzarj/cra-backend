package br.adv.cra.controller;

import br.adv.cra.dto.EmailRequest;
import br.adv.cra.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Sends a simple email message
     * 
     * @param request EmailRequest containing email details
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            if (request.isHtml()) {
                // HTML email sending would be implemented in the service
                return ResponseEntity.badRequest().body("HTML emails not yet implemented in this endpoint");
            } else {
                emailService.sendSimpleMessage(request.getTo(), request.getSubject(), request.getText());
            }
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Sends an email with CC and BCC recipients
     * 
     * @param request EmailRequest containing email details
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/send-advanced")
    public ResponseEntity<String> sendAdvancedEmail(@RequestBody EmailRequest request) {
        try {
            String[] ccArray = request.getCc() != null ? 
                request.getCc().toArray(new String[0]) : null;
            String[] bccArray = request.getBcc() != null ? 
                request.getBcc().toArray(new String[0]) : null;

            emailService.sendMessageWithCCAndBCC(request.getTo(), ccArray, bccArray, request.getSubject(), request.getText());
            
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }
}