package com.github.farzan6118.petclinic.pet.controller;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.pet.dto.request.CreateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.response.SpeciesResponseDto;
import com.github.farzan6118.petclinic.pet.service.SpeciesService;
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
@RequestMapping("/api/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    @GetMapping("/{uuid}")
    public ResponseEntity<SpeciesResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(speciesService.getByUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<SpeciesResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(speciesService.findAll(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(speciesService.findAllIdAndTitle());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateSpeciesRequestDto request) {
        speciesService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid,
                       @Valid @RequestBody UpdateSpeciesRequestDto request) {
        speciesService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        speciesService.delete(uuid);
    }
}
