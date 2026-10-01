package com.github.farzan6118.config;

import com.github.farzan6118.infrastructure.keycloak.config.KeycloakLoginProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakLoginProperties.class)
public class OpenAPIConfig {
    private static final String SECURITY_SCHEME_NAME = "keycloak";

    private final KeycloakLoginProperties keycloakProperties;

    @Bean
    public OpenAPI customOpenAPI() {

        String issuer = keycloakProperties.serverUrl()
                + "/realms/"
                + keycloakProperties.realm();

        return new OpenAPI()
                .info(new Info()
                        .title("Pet Clinic API")
                        .version("1.0.0")
                        .description(
                                "REST API for managing pets, owners, veterinarians, and clinic visits."
                        )
                        .contact(new Contact()
                                .name("Farzan6118")
                                .email("farzan6118@gmail.com")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        createOAuth2Scheme(issuer)
                                )
                );
    }

    private SecurityScheme createOAuth2Scheme(String issuer) {

        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(
                        new OAuthFlows()
                                .authorizationCode(
                                        new OAuthFlow()
                                                .authorizationUrl(
                                                        issuer + "/protocol/openid-connect/auth"
                                                )
                                                .tokenUrl(
                                                        issuer + "/protocol/openid-connect/token"
                                                )
                                                .scopes(
                                                        new Scopes()
                                                                .addString("openid", "OpenID")
                                                                .addString("profile", "Profile")
                                                                .addString("email", "Email")
                                                )
                                )
                );
    }
}