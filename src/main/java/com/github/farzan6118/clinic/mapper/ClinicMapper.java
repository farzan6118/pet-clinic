package com.github.farzan6118.clinic.mapper;

import com.github.farzan6118.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.person.mapper.AddressMapper;
import com.github.farzan6118.person.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClinicMapper {

    private final AddressMapper addressMapper;

    public ClinicResponseDto toDto(Building building) {
        return new ClinicResponseDto(
                building.getUuid(),
                building.getName(),
                building.getCode(),
                addressMapper.toDto(building.getAddress()),
                building.isActive()
        );
    }

    public Building toEntity(CreateClinicRequestDto request) {
        Building building = new Building();
        building.setCode(request.code());
        building.setName(request.name());
        building.setAddress(addressMapper.toEntity(request.address()));
        building.setActive(request.active());
        return building;
    }

    public void toEntity(UpdateClinicRequestDto request, Building building) {
        Address address = building.getAddress();
        building.setName(request.name());
        building.setCode(request.code());
        addressMapper.toEntity(request.address(), address);
        building.setActive(request.active());
    }

    public UuidAndTitleResponseDto toUuidAndTitle(Building building) {
        return new UuidAndTitleResponseDto(
                building.getUuid(),
                String.format("(%d) - %s", building.getCode(), building.getName())
        );
    }
}
