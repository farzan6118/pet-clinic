package com.github.farzan6118.petclinic.person.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record AddressUpdateRequestDto(
        @Size(max = 100)
        @Schema(example = "Home")
        String title,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "Germany")
        String countryName,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "Berlin")
        String provinceName,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "Berlin")
        String cityName,

        @NotBlank
        @Size(max = 20)
        @Schema(example = "12A")
        String buildingNumber,

        @Max(999)
        @Min(-999)
        @Schema(example = "2")
        Integer floor,

        @Size(max = 20)
        @Schema(example = "12A")
        String unitNumber,

        @NotBlank
        @Size(max = 500)
        @Schema(example = "123 Main Street, Tehran")
        String address,

        @Pattern(regexp = "\\d{10}", message = "Postal code must contain exactly 10 digits")
        @Schema(example = "1234567890", minLength = 10, maxLength = 10)
        String postalCode,

        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        @Schema(description = "Latitude in decimal degrees", example = "35.6892")
        Double latitude,

        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        @Schema(description = "Longitude in decimal degrees", example = "51.3890")
        Double longitude,

        @Size(max = 500)
        @Schema(
                description = "Additional address details or directions",
                example = "Near the main entrance, opposite the pharmacy",
                maxLength = 500
        )
        String description,

        @Schema(description = "Whether this is the default address", example = "true")
        boolean defaultAddress
) {
}
