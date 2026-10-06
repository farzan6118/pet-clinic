package com.github.farzan6118.person.mapper;

import com.github.farzan6118.person.dto.request.ContactCreateRequestDto;
import com.github.farzan6118.person.dto.request.ContactUpdateRequestDto;
import com.github.farzan6118.person.dto.response.ContactResponseDto;
import com.github.farzan6118.person.model.Contact;
import com.github.farzan6118.person.model.Person;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public ContactResponseDto toDto(Person person) {
        Contact contact = person.getContact();
        return new ContactResponseDto(
                contact.getEmail(),
                contact.getMobileNumber(),
                contact.getSocialMedia()
        );
    }

    public Contact toEntity(ContactCreateRequestDto request) {
        Contact contact = new Contact();
        contact.setEmail(request.email());
        contact.setMobileNumber(request.mobileNumber());
        contact.setSocialMedia(request.socialMedia());
        return contact;
    }

    public void toEntity(ContactUpdateRequestDto request, Person person) {
        person.getContact().setEmail(request.email());
        person.getContact().setMobileNumber(request.mobileNumber());
        person.getContact().setSocialMedia(request.socialMedia());
    }
}
