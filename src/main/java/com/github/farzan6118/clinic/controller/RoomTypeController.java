package com.github.farzan6118.clinic.controller;

import com.github.farzan6118.clinic.dto.request.CreateRoomTypeRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateRoomTypeRequestDto;
import com.github.farzan6118.clinic.dto.response.RoomTypeResponseDto;
import com.github.farzan6118.clinic.service.RoomTypeService;
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
@RequestMapping("/api/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public ResponseEntity<List<UuidAndTitleResponseDto>> findAllIdAndTitle() {
        return ResponseEntity.ok(roomTypeService.findAllIdAndTitle());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponseDto<RoomTypeResponseDto>> findAll(
            @ModelAttribute @Valid PageAndSortRequestDto requestDto) {
        return ResponseEntity.ok(roomTypeService.findAll(requestDto));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<RoomTypeResponseDto> getByUuid(@PathVariable UUID uuid) {
        return ResponseEntity.ok(roomTypeService.getByUuid(uuid));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreateRoomTypeRequestDto request) {
        roomTypeService.create(request);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody UpdateRoomTypeRequestDto request) {
        roomTypeService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        roomTypeService.delete(uuid);
    }
}

