package com.github.farzan6118.petclinic.appointment.service;

import com.github.farzan6118.petclinic.appointment.dto.request.VisitAdvancedSearch;
import com.github.farzan6118.petclinic.appointment.dto.response.VisitResponseDto;
import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VisitServiceQuery {

    VisitResponseDto findByUuid(UUID uuid);

    PageResponseDto<VisitResponseDto> findAll(PageAndSortRequestDto requestDto);

    PageResponseDto<VisitResponseDto> advancedSearch(VisitAdvancedSearch request);

    List<Visit> findOverlappingVisits(UUID vetUuid, LocalDateTime start, LocalDateTime end);
}
