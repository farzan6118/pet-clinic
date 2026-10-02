package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.response.ClinicAvailabilityResponseDto;

import java.util.List;
import java.time.LocalDate;

public interface ClinicAvailabilityService {
    List<ClinicAvailabilityResponseDto> getAvailability();

    boolean isOpen(LocalDate date);
}
