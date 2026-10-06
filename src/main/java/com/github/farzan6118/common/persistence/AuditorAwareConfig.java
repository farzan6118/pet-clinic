package com.github.farzan6118.common.persistence;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@NullMarked
public class AuditorAwareConfig implements AuditorAware<UUID> {

    @Value("${system.default.uuid}")
    private UUID systemDefaultUuid;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.of(systemDefaultUuid);
    }

}
