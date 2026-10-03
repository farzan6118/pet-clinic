package com.github.farzan6118.infrastructure.keycloak.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakLoginProperties.class)
public class KeycloakConfig {
}
