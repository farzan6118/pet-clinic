package com.github.farzan6118.petclinic.pet.mapper;

import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.pet.dto.request.CreateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.response.SpeciesResponseDto;
import com.github.farzan6118.petclinic.pet.model.Species;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SpeciesMapper {

    public void toEntity(CreateSpeciesRequestDto request, Species species) {
        species.setName(toLower(request.name()));
        species.setCode(toUpper(request.code()));
        species.setOrigin(request.origin());
        species.setDescription(request.description());
    }

    public void toEntity(UpdateSpeciesRequestDto request, Species species) {
        species.setName(toLower(request.name()));
        species.setCode(toUpper(request.code()));
        species.setOrigin(request.origin());
        species.setDescription(request.description());
    }

    public SpeciesResponseDto toDto(Species species) {
        return new SpeciesResponseDto(
                species.getUuid(),
                species.getName(),
                species.getCode(),
                species.getOrigin(),
                species.getDescription()
        );
    }

    public UuidAndTitleResponseDto toUuidAndTitle(Species species) {
        return new UuidAndTitleResponseDto(
                species.getUuid(),
                species.getName()
        );
    }

    private String toUpper(String string) {
        return string.toUpperCase(Locale.ROOT).trim();
    }

    private String toLower(String string) {
        return string.toLowerCase(Locale.ROOT).trim();
    }
}
