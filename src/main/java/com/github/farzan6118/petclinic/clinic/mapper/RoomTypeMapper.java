package com.github.farzan6118.petclinic.clinic.mapper;

import com.github.farzan6118.petclinic.clinic.dto.request.CreateRoomTypeRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.request.UpdateRoomTypeRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.response.RoomTypeResponseDto;
import com.github.farzan6118.petclinic.clinic.model.RoomType;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RoomTypeMapper {

    public RoomTypeResponseDto toDto(RoomType roomType) {
        return new RoomTypeResponseDto(
                roomType.getUuid(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getEntityStatus()
        );
    }

    public void toEntity(CreateRoomTypeRequestDto request, RoomType roomType) {
        roomType.setName(normalizeName(request.name()));
        roomType.setDescription(request.description());
    }

    public void toEntity(UpdateRoomTypeRequestDto request, RoomType roomType) {
        roomType.setName(normalizeName(request.name()));
        roomType.setDescription(request.description());
    }

    public UuidAndTitleResponseDto toUuidAndTitle(RoomType roomType) {
        return new UuidAndTitleResponseDto(
                roomType.getUuid(),
                roomType.getName()
        );
    }

    private String normalizeName(String string) {
        return Objects.requireNonNull(string, "room type name is required").trim();
    }
}
