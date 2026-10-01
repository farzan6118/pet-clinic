package com.github.farzan6118.vet.mapper;

import com.github.farzan6118.clinic.model.Clinic;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.person.mapper.AddressMapper;
import com.github.farzan6118.person.mapper.PersonMapper;
import com.github.farzan6118.person.mapper.ProfileMapper;
import com.github.farzan6118.person.model.Person;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.dto.response.VetResponseDto;
import com.github.farzan6118.vet.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class VetMapper {

    private final PersonMapper personMapper;
    private final ProfileMapper profileMapper;
    private final AddressMapper addressMapper;

    public VetMapper(PersonMapper personMapper, ProfileMapper profileMapper, AddressMapper addressMapper) {
        this.personMapper = personMapper;
        this.profileMapper = profileMapper;
        this.addressMapper = addressMapper;
    }

    public VetResponseDto toDto(Vet vet) {
        Person person = vet.getPerson();
        return new VetResponseDto(
                vet.getUuid(),
                personMapper.toDto(person),
                profileMapper.toDto(person.getProfile()),
                addressMapper.toDto(person.getAddress()),
                vet.getClinic() == null ? null : vet.getClinic().getUuid()
        );
    }

    public Vet toEntity(VetCreateRequestDto request) {
        Vet vet = new Vet();
        Person person = personMapper.toEntity(request.person());
        person.setProfile(profileMapper.toEntity(request.profile()));
        person.setAddress(addressMapper.toEntity(request.address()));
        vet.setPerson(person);
        return vet;
    }

    public Vet toEntity(VetCreateRequestDto request, Clinic clinic) {
        Vet vet = new Vet();
        Person person = personMapper.toEntity(request.person());
        person.setProfile(profileMapper.toEntity(request.profile()));
        person.setAddress(addressMapper.toEntity(request.address()));
        vet.setPerson(person);
        vet.setClinic(clinic);
        return vet;
    }

    public void toEntity(VetUpdateRequestDto request, Vet vet, Clinic clinic) {
        Person person = vet.getPerson();
        personMapper.toEntity(request.person(), person);
        profileMapper.toEntity(request.profile(), person.getProfile());
        addressMapper.toEntity(request.address(), person.getAddress());
        vet.setClinic(clinic);
    }

    public void toEntity(VetUpdateRequestDto request, Vet vet) {
        Person person = vet.getPerson();
        personMapper.toEntity(request.person(), person);
        profileMapper.toEntity(request.profile(), person.getProfile());
        addressMapper.toEntity(request.address(), person.getAddress());
    }

    public UuidAndTitleResponseDto toUuidAndTitle(Vet vet) {
        return new UuidAndTitleResponseDto(
                vet.getUuid(),
                vet.getFullName()
        );
    }
}
