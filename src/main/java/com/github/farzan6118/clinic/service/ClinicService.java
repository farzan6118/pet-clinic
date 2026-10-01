package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.clinic.model.Clinic;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;

import java.util.List;
import java.util.UUID;

public interface ClinicService {
    ClinicResponseDto getByUuid(UUID uuid);

    Clinic getEntityByUuid(UUID uuid);

    Clinic getFirstByActive();

    PageResponseDto<ClinicResponseDto> findAll(PageAndSortRequestDto request);

    List<UuidAndTitleResponseDto> findAllIdAndTitle();

    void create(CreateClinicRequestDto request);

    void update(UUID uuid, UpdateClinicRequestDto request);

    void delete(UUID uuid);

}
