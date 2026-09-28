package com.github.farzan6118.petclinic.owner.service;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerCreateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerUpdateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.response.OwnerResponseDto;
import com.github.farzan6118.petclinic.owner.mapper.OwnerMapper;
import com.github.farzan6118.petclinic.owner.model.Owner;
import com.github.farzan6118.petclinic.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final PageMapper pageMapper;

    @Override
    public OwnerResponseDto getByUuid(UUID uuid) {
        Owner owner = this.getEntityByUuid(uuid);
        return ownerMapper.toDto(owner);
    }

    @Override
    public PageResponseDto<OwnerResponseDto> findAll(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Owner> ownerPage = ownerRepository.findAll(pageable);
        return pageMapper.toPageResponse(ownerPage, ownerMapper::toDto);
    }

    @Override
    @Transactional
    public void create(OwnerCreateRequestDto request) {
        validateUniqueContactInfo(request.profile().mobileNumber(), request.profile().email());
        Owner owner = ownerMapper.toEntity(request);
        ownerRepository.save(owner);
    }

    @Override
    @Transactional
    public void update(UUID uuid, OwnerUpdateRequestDto request) {
        Owner owner = getEntityByUuid(uuid);
        validateEmailUniqueness(request.profile().email(), uuid);
        validateMobileNumberUniqueness(request.profile().mobileNumber(), uuid);
        ownerMapper.toEntity(request, owner);
    }

    private void validateUniqueContactInfo(String mobile, String email) {

        if (ownerRepository.existsByPerson_profile_Email(email)) {
            throw new ConflictException("An owner with this email already exists", "Duplicate owner email");
        }

        if (ownerRepository.existsByPerson_profile_MobileNumber(mobile)) {
            throw new ConflictException("An owner with this mobile number already exists", "Duplicate owner mobile number");
        }
    }

    private void validateEmailUniqueness(String email, UUID uuid) {
        if (ownerRepository.existsByPerson_profile_EmailAndUuidNot(email, uuid)) {
            throw new ConflictException("An owner with this email already exists", "Duplicate owner email");
        }
    }

    private void validateMobileNumberUniqueness(String mobileNumber, UUID uuid) {
        if (ownerRepository.existsByPerson_profile_MobileNumberAndUuidNot(mobileNumber, uuid)) {
            throw new ConflictException("An owner with this mobile number already exists", "Duplicate owner mobile number");
        }
    }

    @Transactional
    @Override
    public void inactivate(UUID uuid) {
        Owner owner = getEntityByUuid(uuid);
        if (owner.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException(
                    "Owner is already inactive",
                    "owner is already inactive"
            );
        }
        owner.setEntityStatus(EntityStatus.INACTIVE);
        log.info("owner inactivated: {}", uuid);
    }

    @Transactional
    @Override
    public void activate(UUID uuid) {
        Owner owner = ownerRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("owner not found"));
        if (owner.getEntityStatus() != EntityStatus.INACTIVE) {
            throw new ConflictException(
                    "Only inactive owners can be activated",
                    "owner cannot be activated"
            );
        }
        owner.setEntityStatus(EntityStatus.ACTIVE);
        log.info("owner activated: {}", uuid);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        Owner owner = this.getEntityByUuid(uuid);
        if (!owner.getEntityStatus().equals(EntityStatus.ACTIVE)) {
            throw new ConflictException(
                    "Owner is already deleted",
                    "owner is already deleted");
        }
        owner.setStatus(EntityStatus.DELETED);
        log.info("owner has been deleted");
    }

    @Override
    public Owner getEntityByUuid(UUID uuid) {
        return ownerRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("owner not found"));
    }

}

