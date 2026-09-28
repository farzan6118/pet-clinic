package com.github.farzan6118.petclinic.appointment.service;

import com.github.farzan6118.petclinic.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.CreateVisitRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.RescheduleVisitRequestDto;

import java.util.UUID;

public interface VisitServiceCommand {

    void bookVisit(CreateVisitRequestDto request);

    void cancelVisit(UUID uuid, String reason);

    void completeVisit(UUID uuid, CompleteVisitRequestDto request);

    void rescheduleVisit(UUID uuid, RescheduleVisitRequestDto request);

}
