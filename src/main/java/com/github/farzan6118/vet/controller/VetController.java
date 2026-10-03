package com.github.farzan6118.vet.controller;

import com.github.farzan6118.appointment.service.DailyAvailabilityService;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.DailyAvailabilityBlockResponseDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetResponseDto;
import com.github.farzan6118.vet.service.VetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;
    private final DailyAvailabilityService dailyAvailabilityService;

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(vetService.findAllIdAndTitle());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<VetResponseDto>> findAllPageable(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(vetService.findAllPageable(requestDto));
    }

    @GetMapping("/{vetUuid}")
    public ResponseEntity<VetResponseDto> getByUuid(@PathVariable UUID vetUuid) {
        return ResponseEntity.ok(vetService.getByUuid(vetUuid));
    }

    @GetMapping("/{vetUuid}/availability")
    public ResponseEntity<List<DailyAvailabilityBlockResponseDto>> getAvailability(
            @PathVariable UUID vetUuid, @RequestParam LocalDate date) {
        return ResponseEntity.ok(dailyAvailabilityService.getVetAvailability(vetUuid, date));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VetCreateRequestDto request) {
        vetService.create(request);
    }

    @PutMapping("/{vetUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID vetUuid, @Valid @RequestBody VetUpdateRequestDto request) {
        vetService.update(vetUuid, request);
    }

    @DeleteMapping("/{vetUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID vetUuid) {
        vetService.delete(vetUuid);
    }

}

