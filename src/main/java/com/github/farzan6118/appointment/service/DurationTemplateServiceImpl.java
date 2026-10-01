package com.github.farzan6118.appointment.service;

import com.github.farzan6118.appointment.dto.request.CreateDurationTemplateRequestDto;
import com.github.farzan6118.appointment.dto.request.UpdateDurationTemplateRequestDto;
import com.github.farzan6118.appointment.dto.response.DurationTemplateResponseDto;
import com.github.farzan6118.appointment.mapper.DurationTemplateMapper;
import com.github.farzan6118.appointment.model.DurationTemplate;
import com.github.farzan6118.appointment.repository.DurationTemplateRepository;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.common.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DurationTemplateServiceImpl implements DurationTemplateService {

    private final DurationTemplateRepository durationTemplateRepository;
    private final DurationTemplateMapper durationTemplateMapper;
    private final PageMapper pageMapper;

    @Override
    public DurationTemplateResponseDto getByUuid(UUID uuid) {
        DurationTemplate durationTemplate = getEntityByUuid(uuid);
        return durationTemplateMapper.mapToDto(durationTemplate);
    }

    @Override
    public DurationTemplate getEntityByUuid(UUID uuid) {
        return durationTemplateRepository.findByUuidAndEntityStatus(uuid, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("duration template not found"));
    }

    @Override
    public PageResponseDto<DurationTemplateResponseDto> findAllPageable(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<DurationTemplate> durationTemplatePage = durationTemplateRepository
                .findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
        return pageMapper.toPageResponse(durationTemplatePage, durationTemplateMapper::mapToDto);
    }

    @Override
    public DurationTemplateResponseDto findByName(String name) {
        DurationTemplate durationTemplate = durationTemplateRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("duration template not found"));
        return durationTemplateMapper.mapToDto(durationTemplate);
    }

    @Override
    public DurationTemplateResponseDto findByDurationMinutes(Integer durationMinutes) {
        DurationTemplate durationTemplate = durationTemplateRepository.findByDurationMinutes(durationMinutes)
                .orElseThrow(() -> new ResourceNotFoundException("duration template not found"));
        return durationTemplateMapper.mapToDto(durationTemplate);
    }

    @Override
    @Cacheable(value = "durationTemplate")
    public List<UuidAndTitleResponseDto> findAllIdAndTitle() {
        return durationTemplateRepository.findAll()
                .stream()
                .map(durationTemplateMapper::toUuidAndTitle)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "durationTemplate")
    public void create(CreateDurationTemplateRequestDto request) {
        validateUniqueFields(request.name(), request.durationMinutes());
        DurationTemplate durationTemplate = new DurationTemplate();
        durationTemplateMapper.mapToEntity(request, durationTemplate);
        durationTemplateRepository.save(durationTemplate);
        log.info("Duration template created: {}", durationTemplate.getUuid());
    }

    private void validateUniqueFields(String name, Integer duration) {
        String nameUpperCase = name.trim().toUpperCase(Locale.ROOT);
        if (durationTemplateRepository.existsByName(nameUpperCase)) {
            throw new ConflictException(
                    "A duration template with this name already exists", "name: " + nameUpperCase + " already exists");
        }

        if (durationTemplateRepository.existsByDurationMinutes(duration)) {
            throw new ConflictException(
                    "A duration template with this duration already exists", "duration " + duration + " already exists");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "durationTemplate")
    public void update(UUID uuid, UpdateDurationTemplateRequestDto request) {
        DurationTemplate durationTemplate = getEntityByUuid(uuid);
        validateNameUniqueness(request.name(), durationTemplate.getUuid());
        validateDurationUniqueness(request.durationMinutes(), durationTemplate.getUuid());
        durationTemplateMapper.mapToEntity(request, durationTemplate);
        log.info("Duration template updated: {}", uuid);
    }

    private void validateNameUniqueness(String name, UUID uuid) {
        String nameUpperCase = name.trim().toUpperCase(Locale.ROOT);
        if (durationTemplateRepository.existsByNameAndUuidNot(nameUpperCase, uuid)) {
            throw new ConflictException("A duration template with this name already exists", "duration template with name: '" + nameUpperCase + "' already exists");
        }
    }

    private void validateDurationUniqueness(Integer duration, UUID uuid) {
        if (durationTemplateRepository.existsByDurationMinutesAndUuidNot(duration, uuid)) {
            throw new ConflictException("A duration template with this duration already exists", "duration template with duration: '" + duration + "' already exists");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "durationTemplate")
    public void delete(UUID uuid) {
        DurationTemplate entityByUuid = this.getEntityByUuid(uuid);
        if (!entityByUuid.getEntityStatus().equals(EntityStatus.ACTIVE)) {
            throw new ConflictException(
                    "Duration template is already deleted",
                    "Duration template already deleted");
        }
        entityByUuid.setEntityStatus(EntityStatus.DELETED);
        log.info("duration template deleted");
    }

}
