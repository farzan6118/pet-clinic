package com.github.farzan6118.petclinic.vet.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.response.VetResponseDto;
import com.github.farzan6118.petclinic.vet.model.Vet;

import java.util.List;
import java.util.UUID;

public interface VetService {

    VetResponseDto getByUuid(UUID uuid);

    Vet getEntityByUuid(UUID uuid);

    PageResponseDto<VetResponseDto> findAllPageable(PageAndSortRequestDto requestDto);

    List<UuidAndTitleResponseDto> findAllIdAndTitle();

//    List<VetAvailableTimeSlot> findAvailableVets(
//            LocalDateTime start,
//            LocalDateTime end);

    void create(VetCreateRequestDto request);

    void update(UUID uuid, VetUpdateRequestDto request);

    void delete(UUID uuid);

    Vet getVetWithUuidLock(UUID vetUuid);
}
