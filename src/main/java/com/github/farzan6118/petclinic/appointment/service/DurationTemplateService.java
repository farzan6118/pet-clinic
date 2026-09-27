package com.github.farzan6118.petclinic.appointment.service;

import com.github.farzan6118.petclinic.appointment.dto.request.CreateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.UpdateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.response.DurationTemplateResponseDto;
import com.github.farzan6118.petclinic.appointment.model.DurationTemplate;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;

import java.util.List;
import java.util.UUID;

public interface DurationTemplateService {
    DurationTemplateResponseDto getByUuid(UUID uuid);

    DurationTemplate getEntityByUuid(UUID uuid);

    PageResponseDto<DurationTemplateResponseDto> findAllPageable(PageAndSortRequestDto requestDto);

    DurationTemplateResponseDto findByName(String name);

    DurationTemplateResponseDto findByDurationMinutes(Integer durationMinutes);

    List<UuidAndTitleResponseDto> findAllIdAndTitle();

    void create(CreateDurationTemplateRequestDto request);

    void update(UUID uuid, UpdateDurationTemplateRequestDto request);

    void delete(UUID uuid);
}
