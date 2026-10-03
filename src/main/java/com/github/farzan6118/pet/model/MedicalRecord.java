package com.github.farzan6118.pet.model;

import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.common.enums.MedicalRecordType;
import com.github.farzan6118.common.persistence.BaseEntity;
import com.github.farzan6118.vet.model.Vet;
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
@Getter
@Setter
@Entity
@Audited
@NoArgsConstructor
@SQLRestriction("entity_status <> 'DELETED'")
public class MedicalRecord extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
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

    @Column(length = 5000)
    @Size(max = 5000)
    private String clinicalNotes;

    @Column(length = 5000)
    @Size(max = 5000)
    private String treatmentPlan;

    @Column(length = 5000)
    @Size(max = 5000)
    private String prescription;

    @Column(nullable = false)
    private boolean followUpRequired;

    private LocalDate followUpDate;

    @Column(length = 2000)
    @Size(max = 2000)
    private String vaccinationDetails;

    @Column(length = 3000)
    @Size(max = 3000)
    private String surgeryDetails;
}
