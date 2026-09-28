package com.github.farzan6118.petclinic.vet.controller;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.petclinic.vet.dto.response.VetResponseDto;
import com.github.farzan6118.petclinic.vet.service.VetService;
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
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    @GetMapping("/{uuid}")
    public ResponseEntity<VetResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(vetService.getByUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<VetResponseDto>> findAllPageable(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(vetService.findAllPageable(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(vetService.findAllIdAndTitle());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VetCreateRequestDto request) {
        vetService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody VetUpdateRequestDto request) {
        vetService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        vetService.delete(uuid);
    }

}

