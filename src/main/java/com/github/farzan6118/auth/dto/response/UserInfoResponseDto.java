package com.github.farzan6118.auth.dto.response;

public record UserInfoResponseDto(
        String givenName,
        String familyName,
        String email,
        String MobileNumber,
        String nationalId
) {
}
