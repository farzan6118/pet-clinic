package com.github.farzan6118.petclinic.clinic.service;

import com.github.farzan6118.petclinic.clinic.dto.request.CreateRoomRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.request.UpdateRoomRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.response.RoomResponseDto;
import com.github.farzan6118.petclinic.clinic.model.Room;
import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.common.enums.VisitCategory;
import com.github.farzan6118.petclinic.common.enums.VisitType;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomResponseDto getByUuid(UUID uuid);

    Room getEntityByUuid(UUID uuid);

    List<UuidAndTitleResponseDto> findAllIdAndTitle();

    PageResponseDto<RoomResponseDto> findAll(PageAndSortRequestDto requestDto);

    void create(CreateRoomRequestDto request);

    void update(UUID uuid, UpdateRoomRequestDto request);

    void delete(UUID uuid);

    Room getAvailableRoomByVisitTypeAndVisitCategory(VisitType visitType, VisitCategory visitCategory);

}
