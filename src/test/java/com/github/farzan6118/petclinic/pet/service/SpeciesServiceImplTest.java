package com.github.farzan6118.petclinic.pet.service;

import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.pet.dto.request.CreateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.dto.request.UpdateSpeciesRequestDto;
import com.github.farzan6118.petclinic.pet.mapper.SpeciesMapper;
import com.github.farzan6118.petclinic.pet.model.Species;
import com.github.farzan6118.petclinic.pet.repository.SpeciesRepository;
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
class SpeciesServiceImplTest {

    @Mock
    private SpeciesRepository speciesRepository;
    @Mock
    private SpeciesMapper speciesMapper;
    @Mock
    private com.github.farzan6118.petclinic.common.mapper.PageMapper pageMapper;
    @InjectMocks
    private SpeciesServiceImpl service;

    @Test
    void create_shouldMapAndSaveWhenCodeIsUnique() {
        CreateSpeciesRequestDto request = new CreateSpeciesRequestDto("Cat", "CAT", "Felis catus", "Pet");
        Species mapped = new Species();
        when(speciesRepository.existsByCodeIgnoreCase("CAT")).thenReturn(false);
        when(speciesRepository.save(any(Species.class))).thenReturn(mapped);

        service.create(request);

        verify(speciesMapper).toEntity(eq(request), any(Species.class));
        verify(speciesRepository).save(any(Species.class));
    }

    @Test
    void update_shouldValidateSubmittedCodeAndRejectDuplicate() {
        UUID uuid = UUID.randomUUID();
        Species species = new Species();
        species.setCode("OLD");
        when(speciesRepository.findByUuid(uuid)).thenReturn(Optional.of(species));
        when(speciesRepository.existsByCodeIgnoreCaseAndUuidNot("NEW", uuid)).thenReturn(true);

        UpdateSpeciesRequestDto request = new UpdateSpeciesRequestDto("Cat", "NEW", "Felis catus", "Pet");
        assertThrows(ConflictException.class, () -> service.update(uuid, request));

        verify(speciesRepository).existsByCodeIgnoreCaseAndUuidNot("NEW", uuid);
        verifyNoInteractions(speciesMapper);
    }

    @Test
    void delete_shouldSoftDeleteActiveSpecies() {
        UUID uuid = UUID.randomUUID();
        Species species = new Species();
        species.setEntityStatus(EntityStatus.ACTIVE);
        when(speciesRepository.findByUuid(uuid)).thenReturn(Optional.of(species));

        service.delete(uuid);

        assertEquals(EntityStatus.DELETED, species.getEntityStatus());
    }
}
