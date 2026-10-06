package com.github.farzan6118.owner.service;

import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.mapper.PageMapper;
import com.github.farzan6118.owner.dto.request.OwnerCreateRequestDto;
import com.github.farzan6118.owner.mapper.OwnerMapper;
import com.github.farzan6118.owner.model.Owner;
import com.github.farzan6118.owner.repository.OwnerRepository;
import com.github.farzan6118.person.dto.request.ContactCreateRequestDto;
import com.github.farzan6118.person.model.Address;
import com.github.farzan6118.person.model.Contact;
import com.github.farzan6118.person.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private OwnerMapper ownerMapper;
    @Mock
    private PageMapper pageMapper;
    @InjectMocks
    private OwnerServiceImpl service;

    @Test
    void create_shouldRejectDuplicateEmail() {
        OwnerCreateRequestDto request = new OwnerCreateRequestDto(null,
                new ContactCreateRequestDto("owner@example.com", "09120000000", null), null);
        when(ownerRepository.existsByPerson_Contact_Email("owner@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));

        verifyNoInteractions(ownerMapper);
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void delete_shouldSoftDeleteOwnerAndNestedPersonGraph() {
        UUID uuid = UUID.randomUUID();
        Owner owner = new Owner();
        Person person = new Person();
        Contact contact = new Contact();
        Address address = new Address();
        owner.setPerson(person);
        person.setContact(contact);
        person.setAddress(address);
        when(ownerRepository.findByUuid(uuid)).thenReturn(Optional.of(owner));

        service.delete(uuid);

        assertEquals(EntityStatus.DELETED, owner.getEntityStatus());
        assertEquals(EntityStatus.DELETED, person.getEntityStatus());
        assertEquals(EntityStatus.DELETED, contact.getEntityStatus());
        assertEquals(EntityStatus.DELETED, address.getEntityStatus());
    }
}
