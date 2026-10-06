package com.github.farzan6118.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClinicProperties.class)
public class ClinicConfiguration {
}