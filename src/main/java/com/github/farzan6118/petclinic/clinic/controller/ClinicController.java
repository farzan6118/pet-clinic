package com.github.farzan6118.petclinic.clinic.controller;

import com.github.farzan6118.petclinic.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.petclinic.clinic.service.ClinicService;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping("/{uuid}")
    public ResponseEntity<ClinicResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(clinicService.getByUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<ClinicResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto request) {
        return ResponseEntity.ok(clinicService.findAll(request));
    }

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(clinicService.findAllIdAndTitle());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateClinicRequestDto request) {
        clinicService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateClinicRequestDto request) {
        clinicService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        clinicService.delete(uuid);
    }
}
