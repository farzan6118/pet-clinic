package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdatePetRequestDto;
import com.github.farzan6118.petclinic.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.petclinic.pet.dto.response.PetResponseDto;
import com.github.farzan6118.petclinic.pet.model.Pet;

import java.util.List;
import java.util.UUID;

public interface PetService {
    PetResponseDto getByUuid(UUID uuid);

    PageResponseDto<PetResponseDto> findAll(PageAndSortRequestDto requestDto);

    void create(CreatePetRequestDto request);

    void update(UUID uuid, UpdatePetRequestDto request);

    void delete(UUID uuid);

    Pet getEntityByUuid(UUID uuid);

    List<PetResponseDto> getPetListByOwnerUuid(UUID uuid);

    MedicalRecordResponseDto getMedicalRecordByPetUuid(UUID uuid);
}
