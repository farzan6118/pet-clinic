package com.github.farzan6118.petclinic.person.mapper;

import com.github.farzan6118.petclinic.person.dto.request.PersonCreateRequestDto;
import com.github.farzan6118.petclinic.person.dto.request.PersonUpdateRequestDto;
import com.github.farzan6118.petclinic.person.dto.response.PersonResponseDto;
import com.github.farzan6118.petclinic.person.model.Person;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PersonMapper {

    public PersonResponseDto toDto(Person person) {
        return new PersonResponseDto(
                person.getTitle(),
                person.getFirstName(),
                person.getLastName(),
                person.getNationalId()
        );
    }

    public Person toEntity(PersonCreateRequestDto request) {
        Person person = new Person();
        toEntity(request, person);
        return person;
    }

    public void toEntity(PersonCreateRequestDto request, Person person) {
        person.setTitle(toUpper(request.title()));
        person.setFirstName(toLower(request.firstName()));
        person.setLastName(toLower(request.lastName()));
        person.setNationalId(request.nationalId());
    }

    public void toEntity(PersonUpdateRequestDto request, Person person) {
        person.setTitle(toUpper(request.title()));
        person.setFirstName(toLower(request.firstName()));
        person.setLastName(toLower(request.lastName()));
        person.setNationalId(request.nationalId());
    }

    private String toUpper(String value) {
        return value == null ? null : value.toUpperCase(Locale.ROOT).trim();
    }

    private String toLower(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT).trim();
    }
}
