package com.github.farzan6118.clinic.controller;

import com.github.farzan6118.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.clinic.service.BuildingService;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
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
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(buildingService.findAllIdAndTitle());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<ClinicResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto request) {
        return ResponseEntity.ok(buildingService.findAll(request));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ClinicResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(buildingService.getByUuid(uuid));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateClinicRequestDto request) {
        buildingService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody UpdateClinicRequestDto request) {
        buildingService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        buildingService.delete(uuid);
    }
}
