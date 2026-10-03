package com.github.farzan6118.appointment.controller;

import com.github.farzan6118.appointment.dto.request.*;
import com.github.farzan6118.appointment.dto.response.AvailableVisitSlotResponseDto;
import com.github.farzan6118.appointment.dto.response.VisitResponseDto;
import com.github.farzan6118.appointment.service.VisitServiceCommand;
import com.github.farzan6118.appointment.service.VisitServiceQuery;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.enums.VisitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitServiceCommand visitServiceCommand;
    private final VisitServiceQuery visitServiceQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void bookVisit(@Valid @RequestBody CreateVisitRequestDto request) {
        visitServiceCommand.bookVisit(request);
    }

    @PutMapping("{uuid}")
    @ResponseStatus(HttpStatus.CREATED)
    public void rescheduleVisit(
            @PathVariable UUID uuid, @Valid @RequestBody RescheduleVisitRequestDto request) {
        visitServiceCommand.rescheduleVisit(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelVisit(@PathVariable UUID uuid, String reason) {
        visitServiceCommand.cancelVisit(uuid, reason);
    }

    @PatchMapping("/{uuid}/complete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void completeVisit(
            @PathVariable UUID uuid, @Valid @RequestBody CompleteVisitRequestDto request) {
        visitServiceCommand.completeVisit(uuid, request);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<VisitResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(visitServiceQuery.findAll(requestDto));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<VisitResponseDto> findByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(visitServiceQuery.findByUuid(uuid));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<VisitResponseDto>> advancedSearch(
            @ModelAttribute("request") @Valid VisitAdvancedSearch request) {
        return ResponseEntity.ok(visitServiceQuery.advancedSearch(request));
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableVisitSlotResponseDto>> findAvailableSlots(
            @RequestParam @NotNull UUID vetUuid,
            @RequestParam @NotNull UUID petUuid,
            @RequestParam @NotNull @FutureOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @NotNull VisitType visitType,
            @RequestParam(defaultValue = "15") @Min(2) @Max(120) Integer durationMinutes,
            @RequestParam(defaultValue = "15") @Min(1) @Max(60) Integer intervalMinutes) {
        AvailableVisitSlotsRequestDto request = new AvailableVisitSlotsRequestDto(
                vetUuid, petUuid, date, visitType, durationMinutes, intervalMinutes);
        return ResponseEntity.ok(visitServiceQuery.findAvailableSlots(request));
    }

    @GetMapping("/available-slots/range")
    public ResponseEntity<List<AvailableVisitSlotResponseDto>> findAvailableSlotsInRange(
            @RequestParam @NotNull UUID vetUuid,
            @RequestParam @NotNull UUID petUuid,
            @RequestParam @NotNull @FutureOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @NotNull @FutureOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam @NotNull VisitType visitType,
            @RequestParam(defaultValue = "15") @Min(2) @Max(120) Integer durationMinutes,
            @RequestParam(defaultValue = "15") @Min(1) @Max(60) Integer intervalMinutes) {
        AvailableVisitSlotsRangeRequestDto request = new AvailableVisitSlotsRangeRequestDto(
                vetUuid, petUuid, dateFrom, dateTo, visitType, durationMinutes, intervalMinutes);
        return ResponseEntity.ok(visitServiceQuery.findAvailableSlots(request));
    }
}

