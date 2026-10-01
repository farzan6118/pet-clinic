package com.github.farzan6118.auth.controller;

import com.github.farzan6118.auth.dto.request.LoginRequestDto;
import com.github.farzan6118.infrastructure.keycloak.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/login")
public class AuthController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequestDto request) {
        AccessTokenResponse response = loginService.getAccessToken(request.username(), request.password());

        return ResponseEntity.ok(response);
    }

}
