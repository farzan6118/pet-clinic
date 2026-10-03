package com.github.farzan6118.person.mapper;

import com.github.farzan6118.person.dto.request.ProfileCreateRequestDto;
import com.github.farzan6118.person.dto.request.ProfileUpdateRequestDto;
import com.github.farzan6118.person.dto.response.ProfileResponseDto;
import com.github.farzan6118.person.model.Contact;
import com.github.farzan6118.person.model.Person;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDto toDto(Person person) {
        Contact contact = person.getContact();
        return new ProfileResponseDto(
                contact.getEmail(),
                contact.getMobileNumber(),
                person.getBirthDate(),
                person.getPhoto()
        );
    }

    public Contact toEntity(ProfileCreateRequestDto request, Person person) {
        Contact contact = new Contact();
        contact.setEmail(request.email());
        contact.setMobileNumber(request.mobileNumber());
        person.setBirthDate(request.birthDate());
        person.setPhoto(request.photo());
        return contact;
    }

    public void toEntity(ProfileUpdateRequestDto request, Person person) {
        person.getContact().setEmail(request.email());
        person.getContact().setMobileNumber(request.mobileNumber());
        person.setBirthDate(request.birthDate());
        person.setPhoto(request.photo());
    }
}
