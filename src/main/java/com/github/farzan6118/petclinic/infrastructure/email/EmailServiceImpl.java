package com.github.farzan6118.petclinic.infrastructure.email;

import com.github.farzan6118.petclinic.common.exception.ExternalServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    /**
     * OWNER — Sent when a pet is due for a periodic checkup.
     */
    @Override
    public void sendOwnerCheckupReminderEmail(String email, String petName, LocalDate checkupDate) {
        Context context = new Context();
        context.setVariable("petName", petName);
        context.setVariable("checkupDate", checkupDate);
        sendHtmlEmail(email, "Periodic Checkup Reminder", "email/checkup-reminder", context);
        log.info("Owner checkup reminder email sent. email={}, petName={}, checkupDate={}", email, petName, checkupDate);
    }

    /**
     * OWNER — Sent when a veterinary visit is scheduled for their pet.
     */
    @Override
    public void sendOwnerVisitScheduledEmail(
            String email, String ownerName, String petName, String petType, LocalDateTime visitDate, String vetName) {
        Context context = new Context();
        context.setVariable("ownerName", ownerName);
        context.setVariable("petName", petName);
        context.setVariable("petType", petType);
        context.setVariable("vetName", vetName);
        context.setVariable("visitDate", visitDate);
        sendHtmlEmail(email, "Veterinary Visit Scheduled", "email/owner/visit-scheduled", context);
        log.info("Owner visit scheduled email sent. email={}, petName={}, visitDate={}", email, petName, visitDate);
    }

    /**
     * VET — Sent when a new veterinary visit is scheduled with the vet.
     */
    @Override
    public void sendVetVisitScheduledEmail(String email, String vetName, LocalDateTime visitDate,
                                           String petName, String spices, String ownerName) {
        Context context = new Context();
        context.setVariable("vetName", vetName);
        context.setVariable("petName", petName);
        context.setVariable("petType", spices);
        context.setVariable("ownerName", ownerName);
        context.setVariable("visitDate", visitDate);
        sendHtmlEmail(email, "New Veterinary Appointment", "email/vet/visit-scheduled", context);
        log.info("Vet visit scheduled email sent. email={}, petName={}, visitDate={}", email, petName, visitDate);
    }

    /**
     * OWNER — Sent when a scheduled veterinary visit is canceled.
     */
    @Override
    public void sendOwnerVisitCancelledEmail(String email, String ownerName, String petName,
                                             LocalDateTime visitDate, String vetName, String reason) {
        Context context = new Context();
        context.setVariable("ownerName", ownerName);
        context.setVariable("petName", petName);
        context.setVariable("vetName", vetName);
        context.setVariable("visitDate", visitDate);
        context.setVariable("reason", reason);
        sendHtmlEmail(email, "Veterinary Visit Cancelled", "email/owner/visit-cancelled", context);
        log.info("Owner visit cancellation email sent. email={}, petName={}, visitDate={}", email, petName, visitDate);
    }

    /**
     * VET — Sent when a scheduled veterinary visit is canceled.
     */
    @Override
    public void sendVetVisitCancelledEmail(String email, String vetName, LocalDateTime visitDate,
                                           String petName, String spices, String ownerName, String reason) {
        Context context = new Context();
        context.setVariable("vetName", vetName);
        context.setVariable("petName", petName);
        context.setVariable("petType", spices);
        context.setVariable("ownerName", ownerName);
        context.setVariable("visitDate", visitDate);
        context.setVariable("reason", reason);
        sendHtmlEmail(email, "Veterinary Appointment Cancelled",
                "email/vet/visit-cancelled", context);
        log.info("Vet visit cancellation email sent. email={}, petName={}, visitDate={}", email, petName, visitDate);
    }

    /**
     * OWNER — Sent when a veterinary visit is rescheduled.
     */
    @Override
    public void sendOwnerVisitRescheduledEmail(String email, String ownerName, String petName,
                                               LocalDateTime oldVisitDate, LocalDateTime newVisitDate, String vetName) {
        Context context = new Context();
        context.setVariable("ownerName", ownerName);
        context.setVariable("petName", petName);
        context.setVariable("vetName", vetName);
        context.setVariable("oldVisitDate", oldVisitDate);
        context.setVariable("newVisitDate", newVisitDate);
        sendHtmlEmail(email, "Veterinary Visit Rescheduled",
                "email/owner/visit-rescheduled", context);
        log.info("Owner visit rescheduled email sent. email={}, petName={}, oldVisitDate={}, newVisitDate={}",
                email, petName, oldVisitDate, newVisitDate);
    }

    /**
     * VET — Sent when a veterinary visit is rescheduled.
     */
    @Override
    public void sendVetVisitRescheduledEmail(String email, String vetName, LocalDateTime oldVisitDate,
                                             LocalDateTime newVisitDate, String petName, String spices, String ownerName) {
        Context context = new Context();
        context.setVariable("vetName", vetName);
        context.setVariable("petName", petName);
        context.setVariable("petType", spices);
        context.setVariable("ownerName", ownerName);
        context.setVariable("oldVisitDate", oldVisitDate);
        context.setVariable("newVisitDate", newVisitDate);
        sendHtmlEmail(email, "Veterinary Appointment Rescheduled",
                "email/vet/visit-rescheduled", context);
        log.info("Vet visit rescheduled email sent. email={}, petName={}, oldVisitDate={}, newVisitDate={}",
                email, petName, oldVisitDate, newVisitDate);
    }

    /**
     * INTERNAL — Common method used by all email operations.
     */
    private void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new ExternalServiceException("Email could not be sent", "Failed to send email");
        }
    }
}





