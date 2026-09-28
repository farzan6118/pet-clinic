package com.github.farzan6118.petclinic.appointment.service;

import com.github.farzan6118.petclinic.appointment.dto.request.CreateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.dto.request.UpdateDurationTemplateRequestDto;
import com.github.farzan6118.petclinic.appointment.mapper.DurationTemplateMapper;
import com.github.farzan6118.petclinic.appointment.model.DurationTemplate;
import com.github.farzan6118.petclinic.appointment.repository.DurationTemplateRepository;
import com.github.farzan6118.petclinic.common.enums.EntityStatus;
import com.github.farzan6118.petclinic.common.exception.ConflictException;
import com.github.farzan6118.petclinic.common.exception.ResourceNotFoundException;
import com.github.farzan6118.petclinic.common.mapper.PageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DurationTemplateServiceImplTest {

    @Mock
    private DurationTemplateRepository repository;
    @Mock
    private DurationTemplateMapper mapper;
    @Mock
    private PageMapper pageMapper;
    @InjectMocks
    private DurationTemplateServiceImpl service;

    @Test
    void create_normalizesNameAndSaves() {
        when(repository.existsByName("STANDARD")).thenReturn(false);
        when(repository.existsByDurationMinutes(20)).thenReturn(false);
        doAnswer(invocation -> {
            mapperReal().mapToEntity(invocation.getArgument(0, CreateDurationTemplateRequestDto.class),
                    invocation.getArgument(1, DurationTemplate.class));
            return null;
        }).when(mapper).mapToEntity(any(CreateDurationTemplateRequestDto.class), any(DurationTemplate.class));

        service.create(new CreateDurationTemplateRequestDto(" standard ", 20, "Standard check"));

        var captor = org.mockito.ArgumentCaptor.forClass(DurationTemplate.class);
        verify(repository).save(captor.capture());
        assertEquals("STANDARD", captor.getValue().getName());
        assertEquals(20, captor.getValue().getDurationMinutes());
        assertEquals("Standard check", captor.getValue().getDescription());
    }

    @Test
    void create_rejectsDuplicateDuration() {
        when(repository.existsByName("STANDARD")).thenReturn(false);
        when(repository.existsByDurationMinutes(20)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.create(new CreateDurationTemplateRequestDto("standard", 20, null)));
        verify(repository, never()).save(any());
    }

    @Test
    void updateChecksSubmittedValuesAndPersistsChanges() {
        UUID uuid = UUID.randomUUID();
        DurationTemplate entity = new DurationTemplate();
        entity.setUuid(uuid);
        entity.setName("OLD");
        entity.setDurationMinutes(10);
        when(repository.findByUuidAndEntityStatus(uuid, EntityStatus.ACTIVE)).thenReturn(Optional.of(entity));
        when(repository.existsByNameAndUuidNot("NEW", uuid)).thenReturn(false);
        when(repository.existsByDurationMinutesAndUuidNot(30, uuid)).thenReturn(false);
        doAnswer(invocation -> {
            mapperReal().mapToEntity(invocation.getArgument(0, UpdateDurationTemplateRequestDto.class),
                    invocation.getArgument(1, DurationTemplate.class));
            return null;
        }).when(mapper).mapToEntity(any(UpdateDurationTemplateRequestDto.class), any(DurationTemplate.class));

        UpdateDurationTemplateRequestDto request = new UpdateDurationTemplateRequestDto("new", 30, "Updated");
        service.update(uuid, request);

        verify(mapper).mapToEntity(request, entity);
        assertEquals("NEW", entity.getName());
        assertEquals(30, entity.getDurationMinutes());
        verify(repository).existsByNameAndUuidNot("NEW", uuid);
        verify(repository).existsByDurationMinutesAndUuidNot(30, uuid);
    }

    @Test
    void getEntityByUuid_reportsMissingOrInactiveTemplate() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByUuidAndEntityStatus(uuid, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getEntityByUuid(uuid));
    }

    private DurationTemplateMapper mapperReal() {
        return new DurationTemplateMapper();
    }
}
