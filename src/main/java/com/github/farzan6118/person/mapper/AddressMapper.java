package com.github.farzan6118.person.mapper;

import com.github.farzan6118.person.dto.request.AddressCreateRequestDto;
import com.github.farzan6118.person.dto.request.AddressUpdateRequestDto;
import com.github.farzan6118.person.dto.response.AddressResponseDto;
import com.github.farzan6118.person.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDto toDto(Address address) {
        return new AddressResponseDto(
                address.getTitle(),
                address.getCountryName(),
                address.getProvinceName(),
                address.getCityName(),
                address.getBuildingNumber(),
                address.getFloor(),
                address.getUnitNumber(),
                address.getAddress(),
                address.getPostalCode(),
                address.getLatitude(),
                address.getLongitude(),
                address.getDescription(),
                address.isDefaultAddress()
        );
    }

    public Address toEntity(AddressCreateRequestDto request) {
        Address address = new Address();
        toEntity(request, address);
        return address;
    }

    public void toEntity(AddressCreateRequestDto request, Address address) {
        address.setTitle(request.title());
        address.setCountryName(request.countryName());
        address.setProvinceName(request.provinceName());
        address.setCityName(request.cityName());
        address.setBuildingNumber(request.buildingNumber());
        address.setFloor(request.floor());
        address.setUnitNumber(request.unitNumber());
        address.setAddress(request.address());
        address.setPostalCode(request.postalCode());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        address.setDescription(request.description());
        address.setDefaultAddress(request.defaultAddress());
    }

    public void toEntity(AddressUpdateRequestDto request, Address address) {
        address.setTitle(request.title());
        address.setCountryName(request.countryName());
        address.setProvinceName(request.provinceName());
        address.setCityName(request.cityName());
        address.setBuildingNumber(request.buildingNumber());
        address.setFloor(request.floor());
        address.setUnitNumber(request.unitNumber());
        address.setAddress(request.address());
        address.setPostalCode(request.postalCode());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        address.setDescription(request.description());
        address.setDefaultAddress(request.defaultAddress());
    }
}
