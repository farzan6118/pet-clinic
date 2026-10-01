package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.response.ClinicAvailabilityResponseDto;

import java.util.List;

public interface ClinicAvailabilityService {
    List<ClinicAvailabilityResponseDto> getAvailability();
}
