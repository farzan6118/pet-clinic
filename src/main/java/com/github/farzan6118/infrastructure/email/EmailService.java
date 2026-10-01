package com.github.farzan6118.infrastructure.email;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface EmailService {
    void sendOwnerCheckupReminderEmail(String email, String petName, LocalDate checkupDate);

    void sendOwnerVisitScheduledEmail(String email, String ownerName, String petName,
                                      String spices, LocalDateTime visitDate, String vetName);

    void sendVetVisitScheduledEmail(String email, String vetName, LocalDateTime visitDate,
                                    String petName, String spices, String ownerName);

    void sendOwnerVisitCancelledEmail(String email, String ownerName, String petName,
                                      LocalDateTime visitDate, String vetName, String reason);

    void sendVetVisitCancelledEmail(String email, String vetName, LocalDateTime visitDate,
                                    String petName, String spices, String ownerName, String reason);

    void sendOwnerVisitRescheduledEmail(String email, String ownerName, String petName, LocalDateTime oldVisitDate, LocalDateTime newVisitDate, String vetName);

    void sendVetVisitRescheduledEmail(String email, String vetName, LocalDateTime oldVisitDate, LocalDateTime newVisitDate, String petName, String spices, String ownerName);
}
