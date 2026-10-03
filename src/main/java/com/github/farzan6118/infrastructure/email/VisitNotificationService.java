package com.github.farzan6118.infrastructure.email;

import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.vet.model.Vet;

import java.time.LocalDateTime;

public interface VisitNotificationService {
    void notifyVisitRescheduled(Visit visit, Pet pet, Vet vet, LocalDateTime oldVisitDate, String reason);

    void notifyCancelVisitParticipants(Visit visit, Pet pet, Vet vet, String reason);

    void notifyBookVisitParticipants(Visit visit, Pet pet, Vet vet);

    void notifyRescheduleVisitParticipants(
            LocalDateTime oldVisitDate, Visit visit, Pet pet, Vet vet, LocalDateTime newVisitStart);
}
