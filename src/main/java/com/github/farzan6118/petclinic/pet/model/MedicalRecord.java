package com.github.farzan6118.petclinic.pet.model;

import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.common.enums.MedicalRecordType;
import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import com.github.farzan6118.petclinic.vet.model.Vet;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * A clinical record created by a veterinarian as the result of a pet visit.
 * <p>
 * This entity intentionally has no service, repository, or controller yet.
 */
@Entity
@Table(name = "medical_records")
@Audited
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class MedicalRecord extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vet_id", nullable = false)
    private Vet vet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MedicalRecordType type;

    @Column(nullable = false, length = 160)
    @Size(max = 160)
    private String title;

    @Column(length = 2000)
    @Size(max = 2000)
    private String diagnosis;

    @Column(name = "clinical_notes", length = 5000)
    @Size(max = 5000)
    private String clinicalNotes;

    @Column(name = "treatment_plan", length = 5000)
    @Size(max = 5000)
    private String treatmentPlan;

    @Column(length = 5000)
    @Size(max = 5000)
    private String prescription;

    @Column(name = "follow_up_required", nullable = false)
    private boolean followUpRequired;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "vaccination_details", length = 2000)
    @Size(max = 2000)
    private String vaccinationDetails;

    @Column(name = "surgery_details", length = 3000)
    @Size(max = 3000)
    private String surgeryDetails;
}
