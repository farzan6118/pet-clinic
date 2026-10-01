package com.github.farzan6118.pet.service;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.pet.dto.request.UpdatePetRequestDto;
import com.github.farzan6118.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.pet.dto.response.PetResponseDto;
import com.github.farzan6118.pet.model.Pet;

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
