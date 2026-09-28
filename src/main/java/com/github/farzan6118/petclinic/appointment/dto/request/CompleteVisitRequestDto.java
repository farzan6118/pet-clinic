package com.github.farzan6118.petclinic.appointment.dto.request;

import com.github.farzan6118.petclinic.common.enums.MedicalRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CompleteVisitRequestDto(
        @Schema(example = "Hashimoto")
        @NotBlank(message = "diagnosis.is.required")
        String diagnosis,
        @Schema(example = "Hashimoto")
        String notes,
        @Schema(example = "PRESCRIPTION")
        MedicalRecordType recordType,
        @Schema(example = "Amoxicillin 250mg, twice daily for 7 days")
        String prescription,
        @Schema(example = "Rest and monitor appetite for the next 48 hours")
        String treatmentPlan,
        @Schema(example = "true")
        boolean followUpRequired,
        @Schema(example = "2026-10-05")
        LocalDate followUpDate,
        @Schema(example = "Rabies booster, valid for one year")
        String vaccinationDetails,
        @Schema(example = "Soft-tissue mass removal recommended within two weeks")
        String surgeryDetails

) {
}
