package com.github.farzan6118.vet.controller;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetResponseDto;
import com.github.farzan6118.vet.service.VetService;
import com.github.farzan6118.appointment.service.DailyAvailabilityService;
import com.github.farzan6118.common.dto.response.DailyAvailabilityBlockResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;
    private final DailyAvailabilityService dailyAvailabilityService;

    @GetMapping("/{uuid}/availability")
    public ResponseEntity<List<DailyAvailabilityBlockResponseDto>> getAvailability(
            @PathVariable UUID uuid, @RequestParam LocalDate date) {
        return ResponseEntity.ok(dailyAvailabilityService.getVetAvailability(uuid, date));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<VetResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(vetService.getByUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<VetResponseDto>> findAllPageable(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(vetService.findAllPageable(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(vetService.findAllIdAndTitle());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VetCreateRequestDto request) {
        vetService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody VetUpdateRequestDto request) {
        vetService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        vetService.delete(uuid);
    }

}

