package com.github.farzan6118.petclinic.clinic.service;

import com.github.farzan6118.petclinic.clinic.dto.response.ClinicAvailabilityResponseDto;
import com.github.farzan6118.petclinic.config.ClinicProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicAvailabilityServiceImpl implements ClinicAvailabilityService {
    private final ClinicProperties clinicProperties;

    @Override
    public List<ClinicAvailabilityResponseDto> getAvailability() {
        ClinicProperties.WorkingHours hours = clinicProperties.workingHours();
        return Arrays.stream(DayOfWeek.values())
                .map(dayOfWeek -> {
                    boolean available = !clinicProperties.closeDays().contains(dayOfWeek);
                    return new ClinicAvailabilityResponseDto(
                            dayOfWeek,
                            available,
                            available ? hours.start() : null,
                            available ? hours.end() : null);
                })
                .toList();
    }
}
