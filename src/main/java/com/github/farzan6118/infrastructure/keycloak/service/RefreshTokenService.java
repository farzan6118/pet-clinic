package com.github.farzan6118.infrastructure.keycloak.service;

import org.keycloak.representations.AccessTokenResponse;

public interface RefreshTokenService {
    AccessTokenResponse refreshToken(String refreshToken);
}
