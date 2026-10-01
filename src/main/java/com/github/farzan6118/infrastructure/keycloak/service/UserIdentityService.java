package com.github.farzan6118.infrastructure.keycloak.service;

import com.github.farzan6118.auth.dto.response.UserInfoResponseDto;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserIdentityService {
    UserInfoResponseDto getCurrentUserInfo(Jwt jwt);
}
