package com.github.farzan6118.petclinic.vet.controller;

import com.github.farzan6118.petclinic.vet.dto.response.VetAvailabilityResponseDto;
import com.github.farzan6118.petclinic.vet.service.VetAvailabilityService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vet-availabilities")
public class VetAvailabilitySearchController {

    private final VetAvailabilityService vetAvailabilityService;

    @GetMapping("/date")
    public ResponseEntity<List<VetAvailabilityResponseDto>> getByDate(
            @NotNull @Schema(example = "2026-12-28")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(vetAvailabilityService.getByDate(date));
    }
}