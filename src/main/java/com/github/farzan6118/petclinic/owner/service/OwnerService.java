package com.github.farzan6118.petclinic.owner.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerCreateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerUpdateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.response.OwnerResponseDto;
import com.github.farzan6118.petclinic.owner.model.Owner;

import java.util.UUID;

public interface OwnerService {
    OwnerResponseDto getByUuid(UUID uuid);

    PageResponseDto<OwnerResponseDto> findAll(PageAndSortRequestDto requestDto);

    void create(OwnerCreateRequestDto request);

    void update(UUID uuid, OwnerUpdateRequestDto request);

    void inactivate(UUID uuid);

    void activate(UUID uuid);

    void delete(UUID uuid);

    Owner getEntityByUuid(UUID uuid);
}
