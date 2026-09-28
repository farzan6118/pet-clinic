package com.github.farzan6118.petclinic.appointment.service;

import com.github.farzan6118.petclinic.appointment.dto.request.VisitAdvancedSearch;
import com.github.farzan6118.petclinic.appointment.dto.response.VisitResponseDto;
import com.github.farzan6118.petclinic.appointment.mapper.VisitMapper;
import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.appointment.repository.VisitRepository;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.exception.BadRequestException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitServiceQueryImpl implements VisitServiceQuery {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final PageMapper pageMapper;

    @Override
    public VisitResponseDto findByUuid(UUID uuid) {
        Visit visit = getVisitByUuid(uuid);
        return visitMapper.toResponse(visit);
    }

    private Visit getVisitByUuid(UUID uuid) {
        return visitRepository.findByUuid(uuid).orElseThrow(
                () -> new ResourceNotFoundException("Visit not found", "Visit not found: " + uuid));
    }

    @Override
    public PageResponseDto<VisitResponseDto> findAll(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Visit> visitPage = visitRepository.findAll(pageable);
        return pageMapper.toPageResponse(visitPage, visitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<VisitResponseDto> advancedSearch(
            VisitAdvancedSearch request) {
        Pageable pageable = pageMapper.getPageable(
                request.pageNumber(), request.pageSize(),
                request.sortBy(), request.sortDirection());
        dateTimeValidation(request);
        Page<Visit> pagedVisit = visitRepository.advancedSearch(request, pageable);
        return pageMapper.toPageResponse(pagedVisit, visitMapper::toResponse);
    }

    private void dateTimeValidation(VisitAdvancedSearch request) {
        if (request.createdDateFrom() != null && request.createdDateTo() != null) {
            if (request.createdDateFrom().isAfter(request.createdDateTo())) {
                throw new BadRequestException(
                        "invalid.created.date.from.created.date.to",
                        "create date from is after create date to"
                );
            }
        }
        if (request.visitDateFrom() != null && request.visitDateTo() != null) {
            if (request.visitDateFrom().isAfter(request.visitDateTo())) {
                throw new BadRequestException(
                        "invalid.visit.date.from.visit.date.to",
                        "visit date from is after visit date to"
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> findOverlappingVisits(UUID vetUuid, LocalDateTime start, LocalDateTime end) {
        return visitRepository.findOverlappingVisits(vetUuid, start, end);
    }

}
