package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.common.enums.MedicalRecordType;
import com.github.farzan6118.petclinic.pet.mapper.MedicalRecordMapper;
import com.github.farzan6118.petclinic.pet.model.MedicalRecord;
import com.github.farzan6118.petclinic.pet.repository.MedicalRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordRepository repository;
    @Mock
    private MedicalRecordMapper mapper;
    @InjectMocks
    private MedicalRecordServiceImpl service;

    @Test
    void createMapsAndPersistsMedicalRecord() {
        MedicalRecord record = new MedicalRecord();
        Visit visit = new Visit();
        CompleteVisitRequestDto request = new CompleteVisitRequestDto(
                "Routine exam", "Notes", MedicalRecordType.CONSULTATION, null,
                "Rest", true, LocalDate.now().plusDays(7), null, null);

        service.create(record, request, visit);

        var order = inOrder(mapper, repository);
        order.verify(mapper).toEntity(record, request, visit);
        order.verify(repository).save(record);
    }
}
