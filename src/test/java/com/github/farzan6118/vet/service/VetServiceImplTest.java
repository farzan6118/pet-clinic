package com.github.farzan6118.vet.service;

import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.clinic.service.BuildingService;
import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.mapper.PageMapper;
import com.github.farzan6118.person.dto.request.AddressCreateRequestDto;
import com.github.farzan6118.person.dto.request.PersonCreateRequestDto;
import com.github.farzan6118.person.dto.request.ProfileCreateRequestDto;
import com.github.farzan6118.person.dto.request.ProfileUpdateRequestDto;
import com.github.farzan6118.person.model.Address;
import com.github.farzan6118.person.model.Contact;
import com.github.farzan6118.person.model.Person;
import com.github.farzan6118.vet.dto.request.VetCreateRequestDto;
import com.github.farzan6118.vet.dto.request.VetUpdateRequestDto;
import com.github.farzan6118.vet.mapper.VetMapper;
import com.github.farzan6118.vet.model.Vet;
import com.github.farzan6118.vet.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;
    @Mock
    private PageMapper pageMapper;
    @Mock
    private VetMapper vetMapper;
    @Mock
    private BuildingService buildingService;
    @InjectMocks
    private VetServiceImpl service;

    @Test
    void create_shouldPersistNestedPersonWhenContactIsUnique() {
        UUID clinicUuid = UUID.randomUUID();
        VetCreateRequestDto request = new VetCreateRequestDto(
                new PersonCreateRequestDto("Dr.", "Sara", "Moradi", "100000001"),
                profileCreate(), new AddressCreateRequestDto("Home", "Germany", "Berlin", "Berlin",
                "1", null, null, "Main Street", "1234567890", 52.52, 13.4, null, true), clinicUuid);
        Vet vet = new Vet();
        Building building = new Building();
        when(buildingService.getEntityByUuid(clinicUuid)).thenReturn(building);
        when(vetMapper.toEntity(request, building)).thenReturn(vet);

        service.create(request);

        verify(vetMapper).toEntity(request, building);
        verify(vetRepository).save(vet);
    }

    @Test
    void update_shouldRejectDuplicateSubmittedEmail() {
        UUID uuid = UUID.randomUUID();
        Vet vet = new Vet();
        when(vetRepository.findByUuid(uuid)).thenReturn(Optional.of(vet));
        when(vetRepository.existsByPerson_Contact_EmailAndUuidNot("new@example.com", uuid)).thenReturn(true);
        VetUpdateRequestDto request = new VetUpdateRequestDto(null,
                new ProfileUpdateRequestDto("new@example.com", "09121111111", null, null), null);

        assertThrows(ConflictException.class, () -> service.update(uuid, request));

        verify(vetRepository).existsByPerson_Contact_EmailAndUuidNot("new@example.com", uuid);
        verifyNoInteractions(vetMapper);
    }

    @Test
    void delete_shouldSoftDeleteVetAndNestedPersonGraph() {
        UUID uuid = UUID.randomUUID();
        Vet vet = new Vet();
        Person person = new Person();
        Contact contact = new Contact();
        Address address = new Address();
        vet.setPerson(person);
        person.setContact(contact);
        person.setAddress(address);
        when(vetRepository.findByUuid(uuid)).thenReturn(Optional.of(vet));

        service.delete(uuid);

        assertEquals(EntityStatus.DELETED, vet.getEntityStatus());
        assertEquals(EntityStatus.DELETED, person.getEntityStatus());
        assertEquals(EntityStatus.DELETED, contact.getEntityStatus());
        assertEquals(EntityStatus.DELETED, address.getEntityStatus());
    }

    private ProfileCreateRequestDto profileCreate() {
        return new ProfileCreateRequestDto("vet@example.com", "09120000000",
                LocalDate.of(1985, 3, 18), null);
    }
}
