package com.github.farzan6118.vet.controller;

import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.vet.dto.request.VetAvailabilityCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetAvailabilityUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.vet.service.VetAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vets/{vetUuid}/availabilities")
public class VetAvailabilityController {

    private final VetAvailabilityService vetAvailabilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAvailability(@Valid @RequestBody VetAvailabilityCreateRequestDto request) {
        vetAvailabilityService.createAvailability(request);
    }

    @PutMapping("/{availabilityUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAvailability(
            @PathVariable UUID availabilityUuid,
            @Valid @RequestBody VetAvailabilityUpdateRequestDto request
    ) {
        vetAvailabilityService.updateAvailability(availabilityUuid, request);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<VetAvailabilityResponseDto>> getVetAvailabilityPageable(
            @PathVariable UUID vetUuid,
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(vetAvailabilityService.getVetAvailabilityPageable(vetUuid, requestDto));
    }

    @DeleteMapping("/{availabilityUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(@PathVariable UUID vetUuid, @PathVariable UUID availabilityUuid) {
        vetAvailabilityService.deleteAvailability(vetUuid, availabilityUuid);
    }
}