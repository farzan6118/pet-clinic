package com.github.farzan6118.petclinic.appointment.mapper;

import com.github.farzan6118.petclinic.appointment.dto.request.CreateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.UpdateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.response.DurationTemplateResponseDto;
import com.github.farzan6118.petclinic.appointment.model.DurationTemplate;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DurationTemplateMapper {

    public void mapToEntity(CreateDurationTemplateRequestDto request, DurationTemplate durationTemplate) {
        durationTemplate.setName(request.name().trim().toUpperCase(Locale.ROOT));
        durationTemplate.setDurationMinutes(request.durationMinutes());
        durationTemplate.setDescription(request.description());
    }

    public void mapToEntity(UpdateDurationTemplateRequestDto request, DurationTemplate durationTemplate) {
        durationTemplate.setName(request.name().trim().toUpperCase(Locale.ROOT));
        durationTemplate.setDurationMinutes(request.durationMinutes());
        durationTemplate.setDescription(request.description());
    }

    public DurationTemplateResponseDto mapToDto(DurationTemplate template) {
        return new DurationTemplateResponseDto(
                template.getUuid(),
                template.getName(),
                template.getDurationMinutes(),
                template.getDescription()
        );
    }

    public UuidAndTitleResponseDto toUuidAndTitle(DurationTemplate durationTemplate) {
        return new UuidAndTitleResponseDto(
                durationTemplate.getUuid(),
                String.format("%s (%d min)",
                        durationTemplate.getName(),
                        durationTemplate.getDurationMinutes())
        );
    }
}
