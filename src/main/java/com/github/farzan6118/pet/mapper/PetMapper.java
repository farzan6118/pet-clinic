package com.github.farzan6118.pet.mapper;

import com.github.farzan6118.owner.model.Owner;
import com.github.farzan6118.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.pet.dto.request.UpdatePetRequestDto;
import com.github.farzan6118.pet.dto.response.PetResponseDto;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.model.Species;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PetMapper {

    public void mapToPet(UpdatePetRequestDto request, Owner owner, Pet pet, Species species) {
        pet.setName(normalizeName(request.name()));
        pet.setColor(request.color());
        pet.setMarks(request.marks());
        pet.setSex(request.sex());
        pet.setBirthDate(request.birthDate());
        pet.setSpecies(species);
        pet.setOwner(owner);
    }

    public void mapToPet(CreatePetRequestDto request, Owner owner, Pet pet, Species species) {
        pet.setName(normalizeName(request.name()));
        pet.setColor(request.color());
        pet.setMarks(request.marks());
        pet.setSex(request.sex());
        pet.setBirthDate(request.birthDate());
        pet.setSpecies(species);
        pet.setOwner(owner);
    }

    public PetResponseDto mapToDto(Pet pet) {
        return new PetResponseDto(
                pet.getUuid(),
                pet.getName(),
                pet.getColor(),
                pet.getMarks(),
                pet.getSex(),
                pet.getBirthDate(),
                pet.getSpecies().getName()
        );
    }

    private String normalizeName(String string) {
        return string.toLowerCase(Locale.ROOT).trim();
    }
}
