package com.github.farzan6118.vet.mapper;

import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.person.mapper.AddressMapper;
import com.github.farzan6118.person.mapper.ContactMapper;
import com.github.farzan6118.person.mapper.PersonMapper;
import com.github.farzan6118.person.model.Person;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetResponseDto;
import com.github.farzan6118.vet.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    private final PersonMapper personMapper;
    private final ContactMapper contactMapper;
    private final AddressMapper addressMapper;

    public VetMapper(PersonMapper personMapper, ContactMapper contactMapper, AddressMapper addressMapper) {
        this.personMapper = personMapper;
        this.contactMapper = contactMapper;
        this.addressMapper = addressMapper;
    }

    public VetResponseDto toDto(Vet vet) {
        Person person = vet.getPerson();
        return new VetResponseDto(
                vet.getUuid(),
                personMapper.toDto(person),
                contactMapper.toDto(person),
                addressMapper.toDto(person.getAddress()),
                vet.getBuilding() == null ? null : vet.getBuilding().getUuid()
        );
    }

    public Vet toEntity(VetCreateRequestDto request) {
        Vet vet = new Vet();
        Person person = personMapper.toEntity(request.person());
        person.setContact(contactMapper.toEntity(request.contact()));
        person.setAddress(addressMapper.toEntity(request.address()));
        vet.setPerson(person);
        return vet;
    }

    public Vet toEntity(VetCreateRequestDto request, Building building) {
        Vet vet = new Vet();
        Person person = personMapper.toEntity(request.person());
        person.setContact(contactMapper.toEntity(request.contact()));
        person.setAddress(addressMapper.toEntity(request.address()));
        vet.setPerson(person);
        vet.setBuilding(building);
        return vet;
    }

    public void toEntity(VetUpdateRequestDto request, Vet vet, Building building) {
        Person person = vet.getPerson();
        personMapper.toEntity(request.person(), person);
        contactMapper.toEntity(request.profile(), person);
        addressMapper.toEntity(request.address(), person.getAddress());
        vet.setBuilding(building);
    }

    public void toEntity(VetUpdateRequestDto request, Vet vet) {
        Person person = vet.getPerson();
        personMapper.toEntity(request.person(), person);
        contactMapper.toEntity(request.profile(), person);
        addressMapper.toEntity(request.address(), person.getAddress());
    }

    public UuidAndTitleResponseDto toUuidAndTitle(Vet vet) {
        return new UuidAndTitleResponseDto(
                vet.getUuid(),
                vet.getFullName()
        );
    }
}
