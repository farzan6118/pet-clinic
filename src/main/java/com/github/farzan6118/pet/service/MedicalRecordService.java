package com.github.farzan6118.pet.service;

import com.github.farzan6118.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.pet.model.MedicalRecord;

import java.util.UUID;

public interface MedicalRecordService {

    void create(MedicalRecord medicalRecord, CompleteVisitRequestDto request, Visit visit);

    MedicalRecord getEntityByPetUuid(UUID petUuid);

    MedicalRecordResponseDto getByPetUuid(UUID petUuid);
}
