package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.petclinic.pet.mapper.MedicalRecordMapper;
import com.github.farzan6118.petclinic.pet.model.MedicalRecord;
import com.github.farzan6118.petclinic.pet.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository repository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    @Transactional
    public void create(MedicalRecord medicalRecord, CompleteVisitRequestDto request, Visit visit) {
        medicalRecordMapper.toEntity(medicalRecord, request, visit);
        repository.save(medicalRecord);
        log.info("Medical Record saved");
    }

    @Override
    public MedicalRecord getEntityByPetUuid(UUID petUuid) {
        return repository.findByPetUuid(petUuid)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord not found"));
    }

    @Override
    public MedicalRecordResponseDto getByPetUuid(UUID petUuid) {
        MedicalRecord entityByPetUuid = getEntityByPetUuid(petUuid);
        return medicalRecordMapper.toDto(entityByPetUuid);
    }
}
