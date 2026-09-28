package com.github.farzan6118.petclinic.common.mapper;

import com.github.farzan6118.petclinic.common.dto.request.PageAndSortRequestDto;
import com.github.farzan6118.petclinic.common.dto.response.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class PageMapper {

    public <T, R> PageResponseDto<R> toPageResponse(
            Page<T> page,
            Function<T, R> mapper
    ) {
        Objects.requireNonNull(page, "page must not be null");
        Objects.requireNonNull(mapper, "mapper must not be null");

        return new PageResponseDto<>(
                page.getContent().stream()
                        .map(mapper)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.getTotalElements(),

                page.getTotalPages(),
                page.getNumberOfElements()
        );
    }

    public Pageable getPageable(PageAndSortRequestDto requestDto) {
        return PageRequest.of(
                requestDto.pageNumber(),
                requestDto.pageSize(),
                Sort.by(requestDto.sortDirection(), requestDto.sortBy())
        );
    }

    public Pageable getPageable(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            Sort.Direction sortDirection) {
        return PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(sortDirection, sortBy)
        );
    }
}

