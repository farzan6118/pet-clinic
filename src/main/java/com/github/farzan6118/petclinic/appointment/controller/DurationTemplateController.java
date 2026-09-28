package com.github.farzan6118.petclinic.appointment.controller;

import com.github.farzan6118.petclinic.appointment.dto.request.CreateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.UpdateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.response.DurationTemplateResponseDto;
import com.github.farzan6118.petclinic.appointment.service.DurationTemplateService;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/duration-templates")
@RequiredArgsConstructor
public class DurationTemplateController {

    private final DurationTemplateService durationTemplateService;

    @GetMapping("/{uuid}")
    public DurationTemplateResponseDto getByUuid(@PathVariable UUID uuid) {
        return durationTemplateService.getByUuid(uuid);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<DurationTemplateResponseDto>> findAllPageable(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(durationTemplateService.findAllPageable(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(durationTemplateService.findAllIdAndTitle());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateDurationTemplateRequestDto request) {
        durationTemplateService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateDurationTemplateRequestDto request
    ) {
        durationTemplateService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        durationTemplateService.delete(uuid);
    }
}