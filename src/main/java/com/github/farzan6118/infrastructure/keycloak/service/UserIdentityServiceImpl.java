package com.github.farzan6118.infrastructure.keycloak.service;

import com.github.farzan6118.auth.dto.response.UserInfoResponseDto;
import com.github.farzan6118.infrastructure.keycloak.config.KeycloakLoginProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class UserIdentityServiceImpl implements UserIdentityService {

    private final RestClient restClient;
    private final KeycloakLoginProperties properties;

    @Override
    public UserInfoResponseDto getCurrentUserInfo(Jwt jwt) {
        return new UserInfoResponseDto(
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("phone_number"),
                jwt.getClaimAsString("national_code")
        );

    }
}