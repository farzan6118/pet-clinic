package com.github.farzan6118.infrastructure.keycloak.client;

import com.github.farzan6118.infrastructure.keycloak.config.KeycloakLoginProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KeycloakTokenClientImpl implements KeycloakTokenClient {

    private final RestClient restClient;
    private final KeycloakLoginProperties properties;

    @Override
    public AccessTokenResponse getAccessToken(
            String username,
            String password
    ) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("username", username);
        form.add("password", password);
        form.add("grant_type", "password");

        return restClient.post()
                .uri(properties.tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(AccessTokenResponse.class);
    }
}