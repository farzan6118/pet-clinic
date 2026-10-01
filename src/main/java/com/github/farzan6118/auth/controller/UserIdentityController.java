package com.github.farzan6118.auth.controller;

import com.github.farzan6118.auth.dto.response.UserInfoResponseDto;
import com.github.farzan6118.infrastructure.keycloak.service.UserIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserIdentityController {

    private final UserIdentityService userIdentityService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userIdentityService.getCurrentUserInfo(jwt)
        );
    }
}
