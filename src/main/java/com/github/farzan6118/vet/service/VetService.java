package com.github.farzan6118.vet.service;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetResponseDto;
import com.github.farzan6118.vet.model.Vet;

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
