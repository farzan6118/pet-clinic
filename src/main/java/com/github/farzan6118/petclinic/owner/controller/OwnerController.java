package com.github.farzan6118.petclinic.owner.controller;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerCreateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.request.OwnerUpdateRequestDto;
import com.github.farzan6118.petclinic.owner.dto.response.OwnerResponseDto;
import com.github.farzan6118.petclinic.owner.service.OwnerService;
import com.github.farzan6118.petclinic.pet.dto.response.PetResponseDto;
import com.github.farzan6118.petclinic.pet.service.PetService;
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
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PetService petService;

    @GetMapping("/{uuid}")
    public ResponseEntity<OwnerResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(ownerService.getByUuid(uuid));
    }

    @GetMapping("/{uuid}/pets")
    public ResponseEntity<List<PetResponseDto>> getPetListByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(petService.getPetListByOwnerUuid(uuid));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<OwnerResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(ownerService.findAll(requestDto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody OwnerCreateRequestDto request) {
        ownerService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody OwnerUpdateRequestDto request) {
        ownerService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        ownerService.delete(uuid);
    }
}
