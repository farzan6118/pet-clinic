package com.github.farzan6118.infrastructure.keycloak.service;

import com.github.farzan6118.infrastructure.keycloak.client.KeycloakTokenClient;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {


    private final KeycloakTokenClient keycloakTokenClient;

    @Override
    public AccessTokenResponse getAccessToken(
            String username,
            String password
    ) {
        return keycloakTokenClient.getAccessToken(username, password);
    }
//
//    private final RestTemplate restTemplate;
//    private final KeycloakLoginProperties keycloakLoginProperties;
//
//    @Override
//    public AccessTokenResponse getAccessToken(String username, String password) {
//        String url = keycloakLoginProperties.serverUrl() +
//                "/realms/" + keycloakLoginProperties.realm() +
//                "/protocol/openid-connect/token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("realm", keycloakLoginProperties.realm());
//        body.add("client_id", keycloakLoginProperties.clientId());
//        body.add("client_secret", keycloakLoginProperties.clientSecret());
//        body.add("username", username);
//        body.add("password", "test");
//        body.add("grant_type", "password");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        return restTemplate.postForObject(url, request, AccessTokenResponse.class);
//    }
}