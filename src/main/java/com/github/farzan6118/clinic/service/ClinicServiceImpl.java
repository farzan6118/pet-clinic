package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.clinic.mapper.ClinicMapper;
import com.github.farzan6118.clinic.model.Clinic;
import com.github.farzan6118.clinic.repository.ClinicRepository;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final PageMapper pageMapper;

    @Override
    public ClinicResponseDto getByUuid(UUID uuid) {
        return clinicMapper.toDto(getEntityByUuid(uuid));
    }

    @Override
    public Clinic getEntityByUuid(UUID uuid) {
        return clinicRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("clinic not found"));
    }

    @Override
    public Clinic getFirstByActive() {
        return clinicRepository.findFirstByActive(true)
                .orElseThrow(() -> new ResourceNotFoundException("clinic not found"));
    }

    @Override
    public PageResponseDto<ClinicResponseDto> findAll(PageAndSortRequestDto request) {
        Pageable pageable = pageMapper.getPageable(request);
        Page<Clinic> clinics = clinicRepository.findAll(pageable);
        return pageMapper.toPageResponse(clinics, clinicMapper::toDto);
    }

    @Override
    @Cacheable(value = "clinic")
    public List<UuidAndTitleResponseDto> findAllIdAndTitle() {
        return clinicRepository.findAll()
                .stream()
                .map(clinicMapper::toUuidAndTitle)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "clinic")
    public void create(CreateClinicRequestDto request) {
        validateUniqueCode(request.code());
        clinicRepository.save(clinicMapper.toEntity(request));
        log.info("clinic created");
    }

    private void validateUniqueCode(Integer code) {
        if (clinicRepository.existsByCode(code)) {
            throw new ConflictException(
                    "clinic with this code already exists",
                    "Duplicate clinic code");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "clinic")
    public void update(UUID uuid, UpdateClinicRequestDto request) {
        Clinic clinic = getEntityByUuid(uuid);
        validateUniqueCodeForUpdate(request.code(), clinic.getUuid());
        clinicMapper.toEntity(request, clinic);
        log.info("clinic updated: {}", uuid);
    }

    private void validateUniqueCodeForUpdate(Integer code, UUID Uuid) {
        if (clinicRepository.existsByCodeAndUuidNot(code, Uuid)) {
            throw new ConflictException(
                    "clinic with this code already exists",
                    "Duplicate clinic code");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "clinic")
    public void delete(UUID uuid) {
        Clinic clinic = getEntityByUuid(uuid);
        if (clinic.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException(
                    "clinic is already inactive",
                    "clinic is already inactive");
        }
        clinic.setEntityStatus(EntityStatus.DELETED);
        log.info("clinic deleted: {}", uuid);
    }
}
