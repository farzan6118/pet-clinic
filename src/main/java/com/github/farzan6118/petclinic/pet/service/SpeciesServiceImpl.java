package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.pet.dto.request.CreateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.response.SpeciesResponseDto;
import com.github.farzan6118.petclinic.pet.mapper.SpeciesMapper;
import com.github.farzan6118.petclinic.pet.model.Species;
import com.github.farzan6118.petclinic.pet.repository.SpeciesRepository;
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
public class SpeciesServiceImpl implements SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;
    private final PageMapper pageMapper;

    @Override
    public SpeciesResponseDto getByUuid(UUID uuid) {
        Species species = getEntityByUuid(uuid);
        return speciesMapper.toDto(species);
    }

    @Override
    public Species getEntityByUuid(UUID uuid) {
        return speciesRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("species not found"));
    }

    @Override
    public PageResponseDto<SpeciesResponseDto> findAll(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Species> speciesPaged = speciesRepository.findAll(pageable);
        return pageMapper.toPageResponse(speciesPaged, speciesMapper::toDto);
    }

    @Override
    @Cacheable(value = "species")
    public List<UuidAndTitleResponseDto> findAllIdAndTitle() {
        return speciesRepository.findAll()
                .stream()
                .map(speciesMapper::toUuidAndTitle)
                .toList();
    }

    @Transactional
    @Override
    @CacheEvict(value = "species")
    public void create(CreateSpeciesRequestDto request) {
        validateCodeUniqueness(request.code());
        Species species = new Species();
        speciesMapper.toEntity(request, species);
        speciesRepository.save(species);
        log.info("species created");
    }

    private void validateCodeUniqueness(String code) {
        String normalizedCode = code.trim();
        if (speciesRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new ConflictException("A species with this code already exists",
                    "species with code '" + code + "' already exists");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "species")
    public void update(UUID uuid, UpdateSpeciesRequestDto request) {
        Species species = this.getEntityByUuid(uuid);
        validateCodeUniqueness(request.code(), uuid);
        speciesMapper.toEntity(request, species);
        log.info("species updated");
    }

    private void validateCodeUniqueness(String code, UUID uuid) {
        String normalizedCode = code.trim();
        if (speciesRepository.existsByCodeIgnoreCaseAndUuidNot(normalizedCode, uuid)) {
            throw new ConflictException("A species with this code already exists", "species with code '" + code + "' already exists");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "species")
    public void delete(UUID uuid) {
        Species species = this.getEntityByUuid(uuid);
        if (species.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException("Species is already inactive", "species is already inactive");
        }
        species.setEntityStatus(EntityStatus.DELETED);
        log.info("species deleted: {}", uuid);
    }


}
