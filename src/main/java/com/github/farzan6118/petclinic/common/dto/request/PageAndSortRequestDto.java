package com.github.farzan6118.petclinic.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

public record PageAndSortRequestDto(

        @NotNull(message = "{page.number.is.required}")
        @Min(value = 0, message = "{page.number.min}")
        @Schema(example = "0")
        Integer pageNumber,

        @NotNull(message = "{page.size.is.required}")
        @Min(value = 1, message = "{page.size.min}")
        @Max(value = 100, message = "{page.size.max}")
        @Schema(example = "10")
        Integer pageSize,

        @Schema(description = "Sort field",
                allowableValues = {"createdDate", "lastModifiedDate"})
        String sortBy,

        @Schema(description = "Sort direction",
                allowableValues = {"ASC", "DESC"})
        Sort.Direction sortDirection
) {

    public PageAndSortRequestDto {
        if (sortDirection == null) {
            sortDirection = Sort.Direction.DESC;
        }
        if (sortBy == null) {
            sortBy = "createdDate";
        }
    }
}