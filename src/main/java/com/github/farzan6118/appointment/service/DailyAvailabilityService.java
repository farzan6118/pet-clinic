package com.github.farzan6118.appointment.service;

import com.github.farzan6118.appointment.model.Visit;
import com.github.farzan6118.appointment.repository.VisitRepository;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.service.ClinicAvailabilityService;
import com.github.farzan6118.clinic.service.RoomService;
import com.github.farzan6118.common.dto.response.DailyAvailabilityBlockResponseDto;
import com.github.farzan6118.common.enums.AvailabilityStatus;
import com.github.farzan6118.common.exception.BadRequestException;
import com.github.farzan6118.config.ClinicProperties;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.model.VetAvailability;
import com.github.farzan6118.vet.service.VetAvailabilityService;
import com.github.farzan6118.vet.service.VetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyAvailabilityService {
    private final VisitRepository visitRepository;
    private final RoomService roomService;
    private final VetService vetService;
    private final VetAvailabilityService vetAvailabilityService;
    private final ClinicAvailabilityService clinicAvailabilityService;
    private final ClinicProperties clinicProperties;

    public List<DailyAvailabilityBlockResponseDto> getRoomAvailability(UUID roomUuid, LocalDate date) {
        Room room = roomService.getEntityByUuid(roomUuid);
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Visit> visits = visitRepository.findOverlappingRoomVisits(roomUuid, dayStart, dayEnd);
        return buildTimeline(date, visits, List.of(), false, true);
    }

    public List<DailyAvailabilityBlockResponseDto> getVetAvailability(UUID vetUuid, LocalDate date) {
        Vet vet = vetService.getEntityByUuid(vetUuid);
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Visit> visits = visitRepository.findOverlappingVisits(vetUuid, dayStart, dayEnd);
        List<VetAvailability> intervals = vetAvailabilityService
                .findActiveByVetUuidAndOverlappingDay(vetUuid, date);
        return buildTimeline(date, visits, intervals, true, false);
    }

    private List<DailyAvailabilityBlockResponseDto> buildTimeline(
            LocalDate date, List<Visit> visits, List<VetAvailability> vetIntervals,
            boolean checkVet, boolean checkClinic) {
        int blockMinutes = clinicProperties.timeBlockMinutes();
        if (blockMinutes <= 0 || 1440 % blockMinutes != 0) {
            throw new BadRequestException("Availability time block must be a positive divisor of 1440 minutes");
        }
        LocalDateTime dayStart = date.atStartOfDay();
        ClinicProperties.WorkingHours hours = clinicProperties.workingHours();
        List<LocalDateTime[]> availabilityWindows;
        if (checkVet) {
            availabilityWindows = vetIntervals.stream()
                    .map(interval -> new LocalDateTime[]{
                            interval.getTimeRange().getStartDateTime().isBefore(dayStart)
                                    ? dayStart : interval.getTimeRange().getStartDateTime(),
                            interval.getTimeRange().getEndDateTime().isAfter(dayStart.plusDays(1))
                                    ? dayStart.plusDays(1) : interval.getTimeRange().getEndDateTime()})
                    .toList();
        } else if (clinicAvailabilityService.isOpen(date)) {
            availabilityWindows = java.util.Collections.singletonList(new LocalDateTime[]{
                    dayStart.with(hours.start()), dayStart.with(hours.end())});
        } else {
            availabilityWindows = List.of();
        }

        return availabilityWindows.stream()
                .flatMap(window -> {
                    LocalDateTime windowStart = window[0];
                    LocalDateTime windowEnd = window[1];
                    long fullBlocks = java.time.Duration.between(windowStart, windowEnd).toMinutes() / blockMinutes;
                    return java.util.stream.LongStream.range(0, fullBlocks)
                            .mapToObj(index -> new LocalDateTime[]{
                                    windowStart.plusMinutes(index * blockMinutes),
                                    windowStart.plusMinutes((index + 1) * blockMinutes)});
                })
                .map(block -> {
                    LocalDateTime start = block[0];
                    LocalDateTime end = block[1];
                    Visit bookedVisit = visits.stream()
                            .filter(visit -> visit.getStartTime().isBefore(end)
                                    && visit.getEndTime().isAfter(start))
                            .findFirst().orElse(null);
                    if (bookedVisit != null) {
                        return new DailyAvailabilityBlockResponseDto(start.toLocalTime(), end.toLocalTime(),
                                AvailabilityStatus.BOOKED, bookedVisit.getUuid());
                    }

                    return new DailyAvailabilityBlockResponseDto(start.toLocalTime(), end.toLocalTime(),
                            AvailabilityStatus.AVAILABLE, null);
                }).toList();
    }
}
