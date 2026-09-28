package com.github.farzan6118.petclinic.person.model;

import com.github.farzan6118.petclinic.common.persistence.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLRestriction("entity_status <> 'DELETED'")
public class Address extends BaseEntity<Long> {

    @Size(max = 100)
    @Schema(description = "title", example = "home", maxLength = 100)
    private String title;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(description = "Country name", example = "country", maxLength = 100)
    private String countryName;


    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(description = "Province or state name", example = "province", maxLength = 100)
    private String provinceName;


    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(description = "City name", example = "city", maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String cityName;


    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    @Schema(description = "Building or house number", example = "12A", maxLength = 20)
    private String buildingNumber;


    @Min(0)
    @Max(99999)
    @Schema(
            description = "Floor number. Ground floor can be represented by 0.",
            example = "2", minimum = "-99", maximum = "9999")
    private Integer floor;

    @Schema(
            description = "Unit or apartment number", example = "12A", maxLength = 20)
    private String unitNumber;


    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    @Schema(description = "Full street address", example = "address", maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String address;


    //    @Pattern(regexp = "^[0-9]{10}$", message = "Postal code must contain exactly 10 digits")
    @Column(length = 10)
    @Schema(
            description = "Postal code", example = "1234567890",
//            pattern = "^[0-9]{10}$",
            minLength = 10, maxLength = 10)
    private String postalCode;


    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column
    @Schema(description = "Latitude in decimal degrees",
            example = "35.6892000", minimum = "-90", maximum = "90")
    private Double latitude;


    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column
    @Schema(description = "Longitude in decimal degrees",
            example = "51.3890000", minimum = "-180", maximum = "180")
    private Double longitude;


    @Size(max = 500)
    @Column(length = 500)
    @Schema(description = "Additional address details or directions",
            example = "Near the main entrance, opposite the pharmacy",
            maxLength = 500
    )
    private String description;

    @Column(nullable = false)
    @Schema(description = "Whether this is the default address",
            example = "true", defaultValue = "true")
    private boolean defaultAddress = true;
}


