package com.github.farzan6118.petclinic.vet.dto.response;

import java.time.LocalTime;

public record TimeInterval(
        LocalTime from,
        LocalTime to
) {
}