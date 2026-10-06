package com.github.farzan6118.pet.mapper;

import com.github.farzan6118.appointment.dto.request.CompleteVisitRequestDto;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.common.enums.MedicalRecordType;
import com.github.farzan6118.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.pet.model.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    public MedicalRecordResponseDto toDto(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }

        return new MedicalRecordResponseDto(
                medicalRecord.getUuid(),
                medicalRecord.getPet().getUuid(),
                medicalRecord.getPet().getName(),
                medicalRecord.getVisit().getUuid(),
                medicalRecord.getVet().getUuid(),
                medicalRecord.getVet().getPerson().getFullName(),
                medicalRecord.getType(),
                medicalRecord.getTitle(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getClinicalNotes(),
                medicalRecord.getTreatmentPlan(),
                medicalRecord.getPrescription(),
                medicalRecord.isFollowUpRequired(),
                medicalRecord.getFollowUpDate(),
                medicalRecord.getVaccinationDetails(),
                medicalRecord.getSurgeryDetails(),
                medicalRecord.getCreatedDate()
        );
    }

    public void toEntity(MedicalRecord medicalRecord, CompleteVisitRequestDto request, Visit visit) {
        medicalRecord.setPet(visit.getPet());
        medicalRecord.setVisit(visit);
        medicalRecord.setVet(visit.getVet());
        medicalRecord.setType(request.recordType() != null
                ? request.recordType()
                : MedicalRecordType.CONSULTATION);
        medicalRecord.setTitle("Visit completion");
        medicalRecord.setDiagnosis(request.diagnosis());
        medicalRecord.setClinicalNotes(request.notes());
        medicalRecord.setTreatmentPlan(request.treatmentPlan());
        medicalRecord.setPrescription(request.prescription());
        medicalRecord.setFollowUpRequired(request.followUpRequired());
        medicalRecord.setFollowUpDate(request.followUpDate());
        medicalRecord.setVaccinationDetails(request.vaccinationDetails());
        medicalRecord.setSurgeryDetails(request.surgeryDetails());
    }
}
