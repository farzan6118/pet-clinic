package com.github.farzan6118.petclinic.clinic.service;

import com.github.farzan6118.petclinic.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.petclinic.clinic.model.Clinic;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;

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
