package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.request.CreateRoomTypeRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateRoomTypeRequestDto;
import com.github.farzan6118.clinic.dto.response.RoomTypeResponseDto;
import com.github.farzan6118.clinic.model.RoomType;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoomTypeService {

    RoomTypeResponseDto getByUuid(UUID uuid);

    RoomType getEntityByUuid(UUID uuid);

    PageResponseDto<RoomTypeResponseDto> findAll(PageAndSortRequestDto requestDto);

    List<UuidAndTitleResponseDto> findAllIdAndTitle();

    void create(CreateRoomTypeRequestDto request);

    void update(UUID uuid, UpdateRoomTypeRequestDto request);

    void delete(UUID uuid);

}
