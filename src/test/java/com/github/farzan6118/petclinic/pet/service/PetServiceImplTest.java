package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.enums.Sex;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import com.github.farzan6118.petclinic.owner.model.Owner;
import com.github.farzan6118.petclinic.owner.service.OwnerService;
import com.github.farzan6118.petclinic.pet.dto.request.CreatePetRequestDto;
import com.github.farzan6118.petclinic.pet.mapper.PetMapper;
import com.github.farzan6118.petclinic.pet.model.Pet;
import com.github.farzan6118.petclinic.pet.model.Species;
import com.github.farzan6118.petclinic.pet.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    private final UUID ownerUuid = UUID.randomUUID();
    private final UUID speciesUuid = UUID.randomUUID();
    @Mock
    private SpeciesService speciesService;
    @Mock
    private PetRepository petRepository;
    @Mock
    private PageMapper pageMapper;
    @Mock
    private PetMapper petMapper;
    @Mock
    private OwnerService ownerService;
    @InjectMocks
    private PetServiceImpl service;

    @Test
    void create_shouldResolveReferencesMapAndSave() {
        Owner owner = new Owner();
        Species species = new Species();
        when(ownerService.getEntityByUuid(ownerUuid)).thenReturn(owner);
        when(speciesService.getEntityByUuid(speciesUuid)).thenReturn(species);
        CreatePetRequestDto request = request();

        service.create(request);

        verify(petMapper).mapToPet(eq(request), eq(owner), any(Pet.class), eq(species));
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void create_shouldRejectDuplicatePetName() {
        Owner owner = new Owner();
        owner.setId(7L);
        when(ownerService.getEntityByUuid(ownerUuid)).thenReturn(owner);
        when(petRepository.existsByOwnerIdAndNameIgnoreCase(7L, "Luna")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request()));

        verifyNoInteractions(speciesService, petMapper);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void create_shouldRejectInactiveOwner() {
        Owner owner = new Owner();
        owner.setEntityStatus(EntityStatus.INACTIVE);
        when(ownerService.getEntityByUuid(ownerUuid)).thenReturn(owner);

        assertThrows(ConflictException.class, () -> service.create(request()));

        verifyNoInteractions(speciesService, petMapper);
        verifyNoInteractions(petRepository);
    }

    private CreatePetRequestDto request() {
        return new CreatePetRequestDto("Luna", "white", null, Sex.FEMALE,
                null, speciesUuid, ownerUuid);
    }
}
