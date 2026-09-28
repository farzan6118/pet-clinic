package com.github.farzan6118.petclinic.common.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponseDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        boolean first,
        boolean last,
        long totalElements,
        int totalPages,
        int size
) {

    public static <T> PageResponseDto<T> of(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumberOfElements());
    }
}
