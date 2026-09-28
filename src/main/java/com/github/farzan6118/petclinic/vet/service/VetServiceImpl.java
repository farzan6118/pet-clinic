package com.github.farzan6118.petclinic.vet.service;

import com.github.farzan6118.petclinic.appointment.model.Visit;
import com.github.farzan6118.petclinic.clinic.model.Clinic;
import com.github.farzan6118.petclinic.clinic.service.ClinicService;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.response.TimeInterval;
import com.github.farzan6118.petclinic.vet.dto.response.VetResponseDto;
import com.github.farzan6118.petclinic.vet.mapper.VetMapper;
import com.github.farzan6118.petclinic.vet.model.Vet;
import com.github.farzan6118.petclinic.vet.repository.VetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final ClinicService clinicService;
    private final VetRepository vetRepository;
    private final PageMapper pageMapper;
    private final VetMapper vetMapper;

    @Override
    public VetResponseDto getByUuid(UUID uuid) {
        Vet vet = getEntityByUuid(uuid);
        return vetMapper.toDto(vet);
    }

    @Override
    public Vet getEntityByUuid(UUID uuid) {
        return vetRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("vet not found"));
    }

    @Override
    public PageResponseDto<VetResponseDto> findAllPageable(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Vet> vetPage = vetRepository.findAll(pageable);
        return pageMapper.toPageResponse(vetPage, vetMapper::toDto);
    }

    @Override
    @Cacheable(value = "vet")
    public List<UuidAndTitleResponseDto> findAllIdAndTitle() {
        return vetRepository.findAll()
                .stream()
                .map(vetMapper::toUuidAndTitle)
                .toList();
    }

    @Override
    public Vet getVetWithUuidLock(UUID vetUuid) {
        return vetRepository.findByUuidWithLock(vetUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Vet not found: " + vetUuid));
    }
//    @Override
//    public List<VetAvailableTimeSlot> findAvailableVets(
//            LocalDateTime start,
//            LocalDateTime end) {
//        List<Vet> vets = vetRepository.findAvailableVets(start, end);
//        return vets.stream()
//                .map(vet -> {
//                    List<Visit> visits = visitServiceQuery
//                            .findOverlappingVisits(vet.getUuid(), start, end);
//
//                    List<TimeInterval> availableIntervals =
//                            calculateAvailableIntervals(start, end, visits);
//
//                    return new VetAvailableTimeSlot(
//                            vet.getUuid(),
//                            vet.getFullName(),
//                            availableIntervals
//                    );
//                })
//                .filter(vet -> !vet.availableIntervals().isEmpty())
//                .toList();
//    }

    private List<TimeInterval> calculateAvailableIntervals(
            LocalDateTime start,
            LocalDateTime end,
            List<Visit> visits) {

        List<TimeInterval> result = new ArrayList<>();

        LocalDateTime current = start;

        for (Visit visit : visits) {

            LocalDateTime visitStart = visit.getStartTime();
            LocalDateTime visitEnd = visit.getEndTime();

            if (current.isBefore(visitStart)) {
                result.add(new TimeInterval(
                        current.toLocalTime(),
                        visitStart.toLocalTime()
                ));
            }

            if (current.isBefore(visitEnd)) {
                current = visitEnd;
            }
        }

        if (current.isBefore(end)) {
            result.add(new TimeInterval(
                    current.toLocalTime(),
                    end.toLocalTime()
            ));
        }

        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "vet")
    public void create(VetCreateRequestDto request) {

        validateUniqueContactInfo(request.profile().mobileNumber(), request.profile().email());
        Clinic clinic = clinicService.getEntityByUuid(request.clinicUuid());
        Vet vet = vetMapper.toEntity(request, clinic);

        vetRepository.save(vet);
        log.info("vet created");
    }

    private void validateUniqueContactInfo(String mobileNumber, String email) {
        if (vetRepository.existsByPerson_Profile_Email(email)) {
            throw new ConflictException("A veterinarian with this email already exists", "Duplicate veterinarian email");
        }
        if (vetRepository.existsByPerson_Profile_MobileNumber(mobileNumber)) {
            throw new ConflictException("A veterinarian with this mobile number already exists", "Duplicate veterinarian mobile number");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "vet")
    public void update(UUID uuid, VetUpdateRequestDto request) {
        Vet vet = getEntityByUuid(uuid);
        validateEmailUniqueness(request.profile().email(), uuid);
        validateTelephoneUniqueness(request.profile().mobileNumber(), uuid);
        vetMapper.toEntity(request, vet);
        log.info("vet updated");
    }

    private void validateEmailUniqueness(String email, UUID vetUuid) {
        if (vetRepository.existsByPerson_Profile_EmailAndUuidNot(email, vetUuid)) {
            throw new ConflictException("A veterinarian with this email already exists", "Duplicate veterinarian email");
        }
    }

    private void validateTelephoneUniqueness(String mobileNumber, UUID vetUuid) {
        if (vetRepository.existsByPerson_Profile_MobileNumberAndUuidNot(mobileNumber, vetUuid)) {
            throw new ConflictException("A veterinarian with this mobile number already exists", "Duplicate veterinarian mobile number");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "vet")
    public void delete(UUID uuid) {
        Vet vet = this.getEntityByUuid(uuid);
        if (!vet.getEntityStatus().equals(EntityStatus.ACTIVE)) {
            throw new ConflictException("Veterinarian is already inactive", "vet is already inactive");
        }
        vet.setStatus(EntityStatus.DELETED);
        log.info("vet deleted: {}", uuid);
    }

}

