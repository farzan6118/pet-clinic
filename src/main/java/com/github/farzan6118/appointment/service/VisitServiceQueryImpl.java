package com.github.farzan6118.appointment.service;

import com.github.farzan6118.appointment.dto.request.AvailableVisitSlotsRangeRequestDto;
import com.github.farzan6118.appointment.dto.request.AvailableVisitSlotsRequestDto;
import com.github.farzan6118.appointment.dto.request.VisitAdvancedSearch;
import com.github.farzan6118.appointment.dto.response.AvailableVisitSlotResponseDto;
import com.github.farzan6118.appointment.dto.response.VisitResponseDto;
import com.github.farzan6118.appointment.mapper.VisitMapper;
import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.appointment.repository.VisitRepository;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.service.RoomService;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.enums.VisitStatus;
import com.github.farzan6118.common.exception.BadRequestException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.common.mapper.PageMapper;
import com.github.farzan6118.config.ClinicProperties;
import com.github.farzan6118.pet.service.PetService;
import com.github.farzan6118.vet.model.VetAvailability;
import com.github.farzan6118.vet.service.VetAvailabilityService;
import com.github.farzan6118.vet.service.VetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitServiceQueryImpl implements VisitServiceQuery {

    private final VetAvailabilityService vetAvailabilityService;
    private final ClinicProperties clinicProperties;
    private final VisitRepository visitRepository;
    private final RoomService roomService;
    private final PetService petService;
    private final VetService vetService;
    private final PageMapper pageMapper;
    private final VisitMapper visitMapper;

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
    public PageResponseDto<VisitResponseDto> advancedSearch(VisitAdvancedSearch request) {
        Pageable pageable = pageMapper.getPageable(
                request.pageNumber(), request.pageSize(),
                request.sortBy(), request.sortDirection());
        validateDateRanges(request);
        Page<Visit> pagedVisit = visitRepository.advancedSearch(request, pageable);
        return pageMapper.toPageResponse(pagedVisit, visitMapper::toResponse);
    }

    private void validateDateRanges(VisitAdvancedSearch request) {
        validateDateRange(request.createdDateFrom(), request.createdDateTo(),
                "invalid.created.date.from.created.date.to",
                "create date from is after create date to");
        validateDateRange(request.visitDateFrom(), request.visitDateTo(),
                "invalid.visit.date.from.visit.date.to",
                "visit date from is after visit date to");
    }

    @Override
    public List<AvailableVisitSlotResponseDto> findAvailableSlots(AvailableVisitSlotsRequestDto request) {
        vetService.getEntityByUuid(request.vetUuid());
        petService.getEntityByUuid(request.petUuid());
        int blockMinutes = clinicProperties.timeBlockMinutes();
        if (blockMinutes <= 0) {
            throw new BadRequestException("Availability time block must be positive");
        }
        int durationMinutes = Math.multiplyExact(
                (request.durationMinutes() - 1) / blockMinutes + 1, blockMinutes);

        Room room = roomService.getRoomForAvailabilitySearch(request.visitType());
        LocalDateTime dayStart = request.date().atStartOfDay();
        LocalDateTime dayEnd = request.date().plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestStart = dayStart.isBefore(now) ? now : dayStart;
        if (earliestStart.getSecond() != 0 || earliestStart.getNano() != 0) {
            earliestStart = earliestStart.withSecond(0).withNano(0).plusMinutes(1);
        }
        List<AvailableVisitSlotResponseDto> slots = new ArrayList<>();

        if (clinicProperties.closeDays().contains(request.date().getDayOfWeek())) {
            return slots;
        }

        List<Visit> vetVisits = visitRepository.findAllVisitsByVetUuidAndStartTimeBetween(
                request.vetUuid(), dayStart, dayEnd);
        List<Visit> petVisits = visitRepository.findAllVisitsByPetUuidAndStartTimeBetween(
                request.petUuid(), dayStart, dayEnd);
        List<Visit> roomVisits = room == null ? List.of()
                : visitRepository.findAllVisitsByRoomUuidAndStartTimeBetween(
                room.getUuid(), dayStart, dayEnd);

        for (VetAvailability availability : vetAvailabilityService
                .findActiveByVetUuidAndOverlappingDay(request.vetUuid(), request.date())) {
            LocalDateTime start = availability.getTimeRange().getStartDateTime();
            if (start.isBefore(earliestStart)) {
                long minutesFromAvailability = Duration.between(start, earliestStart).toMinutes();
                long intervals = (minutesFromAvailability + request.intervalMinutes() - 1)
                        / request.intervalMinutes();
                start = start.plusMinutes(intervals * request.intervalMinutes());
            }

            while (!start.plusMinutes(durationMinutes)
                    .isAfter(availability.getTimeRange().getEndDateTime())) {
                LocalDateTime end = start.plusMinutes(durationMinutes);
                if (start.isBefore(dayEnd)
                        && isWithinClinicHours(start, end)
                        && isAvailable(vetVisits, petVisits, roomVisits, start, end)) {
                    slots.add(new AvailableVisitSlotResponseDto(
                            start, end, room == null ? null : room.getUuid()));
                }
                start = start.plusMinutes(request.intervalMinutes());
            }
        }

        return slots.stream()
                .distinct()
                .sorted(Comparator.comparing(AvailableVisitSlotResponseDto::visitDateFrom))
                .toList();
    }

    @Override
    public List<AvailableVisitSlotResponseDto> findAvailableSlots(AvailableVisitSlotsRangeRequestDto request) {
        long days = ChronoUnit.DAYS.between(request.dateFrom(), request.dateTo());
        if (days < 0 || days > 30) {
            throw new BadRequestException("Date range must be between 1 and 31 days");
        }

        List<AvailableVisitSlotResponseDto> slots = new ArrayList<>();
        for (LocalDateTime date = request.dateFrom().atStartOfDay();
             !date.toLocalDate().isAfter(request.dateTo());
             date = date.plusDays(1)) {
            slots.addAll(findAvailableSlots(new AvailableVisitSlotsRequestDto(
                    request.vetUuid(), request.petUuid(), date.toLocalDate(), request.visitType(),
                    request.durationMinutes(), request.intervalMinutes())));
        }
        return slots;
    }

    private boolean isWithinClinicHours(LocalDateTime start, LocalDateTime end) {
        ClinicProperties.WorkingHours hours = clinicProperties.workingHours();
        return !start.toLocalTime().isBefore(hours.start())
                && !end.toLocalTime().isAfter(hours.end());
    }

    private boolean isAvailable(
            List<Visit> vetVisits, List<Visit> petVisits, List<Visit> roomVisits,
            LocalDateTime start, LocalDateTime end) {
        return vetVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, false))
                && petVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, false))
                && roomVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, true));
    }

    private boolean isVetAvailable(List<Visit> vetVisits, LocalDateTime start, LocalDateTime end){
        return vetVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, false));
    }

    private boolean isPetAvailable(List<Visit> petVisits, LocalDateTime start, LocalDateTime end){
        return petVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, false));
    }

    private boolean isRoomAvailable(List<Visit> roomVisits, LocalDateTime start, LocalDateTime end){
        return roomVisits.stream().noneMatch(visit -> overlapsActiveVisit(visit, start, end, true));
    }

    private boolean overlapsActiveVisit(
            Visit visit, LocalDateTime start, LocalDateTime end, boolean roomReservation) {
        if (visit.getStatus() == VisitStatus.CANCELLED || visit.getStatus() == VisitStatus.COMPLETED) {
            return false;
        }
        if (roomReservation) {
            return !visit.getStartTime().isAfter(end) && visit.getEndTime().isAfter(start);
        }
        return visit.getStartTime().isBefore(end) && visit.getEndTime().isAfter(start);
    }

    private <T extends Comparable<? super T>> void validateDateRange(
            T from, T to, String errorCode, String message) {
        if (from != null && to != null && from.compareTo(to) > 0) {
            throw new BadRequestException(errorCode, message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> findOverlappingVisits(UUID vetUuid, LocalDateTime start, LocalDateTime end) {
        return visitRepository.findOverlappingVisits(vetUuid, start, end);
    }

}
