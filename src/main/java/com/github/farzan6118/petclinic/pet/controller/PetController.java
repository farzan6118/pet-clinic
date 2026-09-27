package com.github.farzan6118.petclinic.pet.controller;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdatePetRequestDto;
import com.github.farzan6118.petclinic.pet.dto.response.MedicalRecordResponseDto;
import com.github.farzan6118.petclinic.pet.dto.response.PetResponseDto;
import com.github.farzan6118.petclinic.pet.service.PetService;
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
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    @GetMapping("/{uuid}")
    public ResponseEntity<PetResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(petService.getByUuid(uuid));
    }

    @GetMapping("/{uuid}/medical-record")
    public ResponseEntity<MedicalRecordResponseDto> getMedicalRecordByPetUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(petService.getMedicalRecordByPetUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<PetResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(petService.findAll(requestDto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreatePetRequestDto request) {
        petService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdatePetRequestDto request
    ) {
        petService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        petService.delete(uuid);
    }
}

