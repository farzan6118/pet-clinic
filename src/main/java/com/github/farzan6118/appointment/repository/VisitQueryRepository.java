package com.github.farzan6118.appointment.repository;

import com.github.farzan6118.appointment.dto.request.VisitAdvancedSearch;
import com.github.farzan6118.appointment.model.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VisitQueryRepository {

    Page<Visit> advancedSearch(VisitAdvancedSearch request, Pageable pageable);

    List<Visit> findOverlappingVisits(
            UUID vetUuid,
            LocalDateTime start,
            LocalDateTime end);

    List<Visit> findOverlappingRoomVisits(UUID roomUuid, LocalDateTime start, LocalDateTime end);
}
