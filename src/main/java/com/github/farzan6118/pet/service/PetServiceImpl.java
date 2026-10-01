package com.github.farzan6118.pet.service;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.common.mapper.PageMapper;
import com.github.farzan6118.owner.model.Owner;
import com.github.farzan6118.owner.service.OwnerService;
import com.github.farzan6118.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.pet.dto.request.UpdatePetRequestDto;
import com.github.farzan6118.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.pet.dto.response.PetResponseDto;
import com.github.farzan6118.pet.mapper.MedicalRecordMapper;
import com.github.farzan6118.pet.mapper.PetMapper;
import com.github.farzan6118.pet.model.MedicalRecord;
import com.github.farzan6118.pet.model.Pet;
import com.github.farzan6118.pet.model.Species;
import com.github.farzan6118.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class PetServiceImpl implements PetService {

    private final SpeciesService speciesService;
    private final PetRepository petRepository;
    private final PageMapper pageMapper;
    private final PetMapper petMapper;
    private final OwnerService ownerService;
    private final MedicalRecordService medicalRecordService;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    public PetResponseDto getByUuid(UUID uuid) {
        Pet pet = this.getEntityByUuid(uuid);
        return petMapper.mapToDto(pet);
    }


    @Override
    public PageResponseDto<PetResponseDto> findAll(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Pet> petPage = petRepository.findAll(pageable);
        return pageMapper.toPageResponse(petPage, petMapper::mapToDto);
    }

    @Override
    @Transactional
    public void create(CreatePetRequestDto request) {
        Owner owner = ownerService.getEntityByUuid(request.ownerUuid());
        validateOwnerActive(owner);
        validateUniqueness(owner.getId(), request.name());
        Species species = speciesService.getEntityByUuid(request.speciesUuid());
        validateSpeciesActive(species);
        Pet pet = new Pet();
        petMapper.mapToPet(request, owner, pet, species);
        petRepository.save(pet);
        log.info("pet created");
    }

    private void validateUniqueness(Long ownerId, String name) {
        if (petRepository.existsByOwnerIdAndNameIgnoreCase(ownerId, name.trim())) {
            throw new ConflictException("This owner already has a pet with that name", "owner already has a pet named '" + name + "'");
        }
    }

    @Transactional
    @Override
    public void update(UUID uuid, UpdatePetRequestDto request) {
        Pet pet = this.getEntityByUuid(uuid);
        Owner owner = ownerService.getEntityByUuid(request.ownerUuid());
        validateOwnerActive(owner);
        validateUniqueness(owner.getId(), request.name(), pet.getId());
        Species species = speciesService.getEntityByUuid(request.speciesUuid());
        validateSpeciesActive(species);
        petMapper.mapToPet(request, owner, pet, species);
        log.info("pet updated");
    }

    private void validateUniqueness(Long ownerId, String name, Long petId) {
        if (petRepository.existsByOwnerIdAndNameIgnoreCaseAndIdNot(ownerId, name.trim(), petId)) {
            throw new ConflictException("This owner already has a pet with that name", "owner already has a pet named '" + name + "'");
        }
    }

    private void validateOwnerActive(Owner owner) {
        if (owner.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException("The pet owner must be active", "pet owner must be active");
        }
    }

    private void validateSpeciesActive(Species species) {
        if (species.getEntityStatus() != EntityStatus.ACTIVE) {
            throw new ConflictException("The pet species must be active", "pet species must be active");
        }
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        Pet pet = this.getEntityByUuid(uuid);
        if (!pet.getEntityStatus().equals(EntityStatus.ACTIVE)) {
            throw new ConflictException("Pet is already inactive", "pet is already inactive");
        }
        pet.setEntityStatus(EntityStatus.DELETED);
        log.info("pet deleted: {}", uuid);
    }

    @Override
    public Pet getEntityByUuid(UUID uuid) {
        return petRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("pet not found"));
    }

    @Override
    public List<PetResponseDto> getPetListByOwnerUuid(UUID uuid) {
        return petRepository.findByOwnerUuid(uuid)
                .stream()
                .map(petMapper::mapToDto)
                .toList();
    }

    @Override
    public MedicalRecordResponseDto getMedicalRecordByPetUuid(UUID uuid) {
        MedicalRecord entityByPetUuid = medicalRecordService.getEntityByPetUuid(uuid);
        return medicalRecordMapper.toDto(entityByPetUuid);
    }
}

