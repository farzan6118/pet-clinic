package com.github.farzan6118.infrastructure.keycloak.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakLoginProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret
) {
    public String tokenEndpoint() {
        return "%s/realms/%s/protocol/openid-connect/token"
                .formatted(serverUrl, realm);
    }

    public String userEndpoint() {
        return serverUrl
                + "/admin/realms/" + realm
                + "/users";
    }

    public String userInfoEndpoint() {
        return serverUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/userinfo";
    }
}

