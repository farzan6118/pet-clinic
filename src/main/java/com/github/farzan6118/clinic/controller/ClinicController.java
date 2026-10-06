package com.github.farzan6118.clinic.controller;

import com.github.farzan6118.clinic.dto.response.ClinicAvailabilityResponseDto;
import com.github.farzan6118.clinic.service.ClinicAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clinic")
public class ClinicController {

    private final ClinicAvailabilityService clinicAvailabilityService;

    @GetMapping("/availability")
    public ResponseEntity<List<ClinicAvailabilityResponseDto>> getAvailability() {
        return ResponseEntity.ok(clinicAvailabilityService.getAvailability());
    }

}
