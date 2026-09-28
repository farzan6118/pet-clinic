package com.github.farzan6118.petclinic.clinic.mapper;

import com.github.farzan6118.petclinic.clinic.dto.request.CreateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.request.UpdateClinicRequestDto;
import com.github.farzan6118.petclinic.clinic.dto.response.ClinicResponseDto;
import com.github.farzan6118.petclinic.clinic.model.Clinic;
import com.github.farzan6118.petclinic.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.petclinic.person.mapper.AddressMapper;
import com.github.farzan6118.petclinic.person.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClinicMapper {

    private final AddressMapper addressMapper;

    public ClinicResponseDto toDto(Clinic clinic) {
        return new ClinicResponseDto(
                clinic.getUuid(),
                clinic.getName(),
                clinic.getCode(),
                addressMapper.toDto(clinic.getAddress()),
                clinic.isActive()
        );
    }

    public Clinic toEntity(CreateClinicRequestDto request) {
        Clinic clinic = new Clinic();
        clinic.setCode(request.code());
        clinic.setName(request.name());
        clinic.setAddress(addressMapper.toEntity(request.address()));
        clinic.setActive(request.active());
        return clinic;
    }

    public void toEntity(UpdateClinicRequestDto request, Clinic clinic) {
        Address address = clinic.getAddress();
        clinic.setName(request.name());
        clinic.setCode(request.code());
        addressMapper.toEntity(request.address(), address);
        clinic.setActive(request.active());
    }

    public UuidAndTitleResponseDto toUuidAndTitle(Clinic clinic) {
        return new UuidAndTitleResponseDto(
                clinic.getUuid(),
                String.format("(%d) - %s", clinic.getCode(), clinic.getName())
        );
    }
}
