package com.github.farzan6118.clinic.service;

import com.github.farzan6118.clinic.dto.request.CreateRoomRequestDto;
import com.github.farzan6118.clinic.dto.request.UpdateRoomRequestDto;
import com.github.farzan6118.clinic.dto.response.RoomResponseDto;
import com.github.farzan6118.clinic.mapper.RoomMapper;
import com.github.farzan6118.clinic.model.Building;
import com.github.farzan6118.clinic.model.Room;
import com.github.farzan6118.clinic.model.RoomType;
import com.github.farzan6118.clinic.repository.RoomRepository;
import com.github.farzan6118.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.common.dto.response.PageResponseDto;
import com.github.farzan6118.common.dto.response.UuidAndTitleResponseDto;
import com.github.farzan6118.common.enums.EntityStatus;
import com.github.farzan6118.common.enums.VisitCategory;
import com.github.farzan6118.common.enums.VisitType;
import com.github.farzan6118.common.exception.ConflictException;
import com.github.farzan6118.common.exception.ResourceNotFoundException;
import com.github.farzan6118.common.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final PageMapper pageMapper;
    private final RoomTypeService roomTypeService;
    private final BuildingService buildingService;

    @Override
    public RoomResponseDto getByUuid(UUID uuid) {
        Room room = getEntityByUuid(uuid);
        return roomMapper.mapToDto(room);
    }

    @Override
    public Room getEntityByUuid(UUID uuid) {
        return roomRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("room not found"));
    }

    @Override
    public Room getEntityByUuidForUpdate(UUID uuid) {
        return roomRepository.findByUuidForUpdate(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("room not found"));
    }

    @Override
    public PageResponseDto<RoomResponseDto> findAll(PageAndSortRequestDto requestDto) {
        Pageable pageable = pageMapper.getPageable(requestDto);
        Page<Room> roomPage = roomRepository.findAll(pageable);
        return pageMapper.toPageResponse(roomPage, roomMapper::mapToDto);
    }


    @Override
    @Cacheable(value = "room")
    public List<UuidAndTitleResponseDto> findAllIdAndTitle() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toUuidAndTitle)
                .toList();
    }

    @Override
    public Room getAvailableRoomByVisitTypeAndVisitCategory(VisitType visitType, VisitCategory visitCategory) {
        List<String> roomTypeNames = switch (visitType) {
            case ONSITE -> List.of("examination", "individual");
            case ONLINE, OFFSITE -> List.of();
        };

        if (roomTypeNames.isEmpty()) {
            return null;
        }

        return roomRepository.findActiveRoomsByTypeNames(roomTypeNames)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "visit.room.not.available",
                        "No room is available for the selected visit type and time"));
    }

    @Override
    public Room getRoomForAvailabilitySearch(VisitType visitType) {
        List<String> roomTypeNames = switch (visitType) {
            case ONSITE -> List.of("examination", "individual");
            case ONLINE, OFFSITE -> List.of();
        };

        if (roomTypeNames.isEmpty()) {
            return null;
        }

        return roomRepository.findActiveRoomsByTypeNamesWithoutLock(roomTypeNames)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "visit.room.not.available",
                        "No room is available for the selected visit type and time"));
    }


    @Transactional
    @Override
    @CacheEvict(value = "room")
    public void create(CreateRoomRequestDto request) {

        validateCodeUniqueness(request.roomNumber());
        var roomType = roomTypeService.getEntityByUuid(request.roomTypeUuid());
        var clinic = buildingService.getEntityByUuid(request.clinicUuid());
        validateActiveReferences(roomType, clinic);
        Room room = new Room();
        roomMapper.mapToEntity(request, room, roomType, clinic);

        roomRepository.save(room);
        log.info("room created");

    }

    private void validateCodeUniqueness(String roomNumber) {
        String normalizedCode = roomNumber.trim();
        if (roomRepository.existsByRoomNumberIgnoreCase(normalizedCode)) {
            throw new ConflictException("A room with this number already exists", "room number '" + roomNumber + "' already exists");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "room")
    public void update(UUID uuid, UpdateRoomRequestDto request) {
        Room room = getEntityByUuid(uuid);
        validateCodeUniqueness(request.roomNumber(), uuid);
        var roomType = roomTypeService.getEntityByUuid(request.roomTypeUuid());
        var clinic = buildingService.getEntityByUuid(request.clinicUuid());
        validateActiveReferences(roomType, clinic);
        roomMapper.mapToEntity(request, room, roomType, clinic);
        log.info("room updated");
    }

    private void validateCodeUniqueness(String code, UUID roomUuid) {
        String normalizedCode = code.trim();
        if (roomRepository.existsByRoomNumberIgnoreCaseAndUuidNot(normalizedCode, roomUuid)) {
            throw new ConflictException("A room with this number already exists", "room number '" + code + "' already exists");
        }
    }

    private void validateActiveReferences(RoomType roomType, Building building) {
        if (roomType.getEntityStatus() != EntityStatus.ACTIVE
                || building.getEntityStatus() != EntityStatus.ACTIVE
                || !building.isActive()) {
            throw new ConflictException("The room type and building must be active", "room type and building must be active");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = "room")
    public void delete(UUID uuid) {
        Room room = this.getEntityByUuid(uuid);
        if (!room.getEntityStatus().equals(EntityStatus.ACTIVE)) {
            throw new ConflictException("Room is already inactive", "room is already inactive");
        }
        room.setEntityStatus(EntityStatus.DELETED);
        log.info("room deleted: {}", uuid);
    }
}

