package com.github.farzan6118.appointment.mapper;

import com.github.farzan6118.appointment.dto.request.CreateVisitRequestDto;
import com.github.farzan6118.appointment.dto.response.VisitResponseDto;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.common.enums.VisitStatus;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.vet.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {
    public VisitResponseDto toResponse(Visit visit) {
        if (visit == null) {
            return null;
        }
        return new VisitResponseDto(visit.getUuid(),
                visit.getPet().getUuid(),
                visit.getPet().getName(),
                visit.getPet().getSpecies().getName(),
                visit.getPet().getOwner().getPerson().getFullName(),
                visit.getVet().getUuid(),
                visit.getVet().getPerson().getFullName(),
                visit.getStartTime(),
                visit.getEndTime(),
                visit.getVisitType(),
                visit.getRoom() == null ? null : visit.getRoom().getUuid(),
                visit.getDescription(),
                visit.getStatus()
        );
    }

    public Visit mapToVisitEntity(CreateVisitRequestDto request, Pet pet, Vet vet) {
        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setVet(vet);
        visit.setVisitType(request.visitType());
        visit.setDescription(request.description());
        visit.setStatus(VisitStatus.SCHEDULED);
        return visit;
    }
}