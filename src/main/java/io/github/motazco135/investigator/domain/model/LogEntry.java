package io.github.motazco135.investigator.domain.model;

import java.time.Instant;

public record LogEntry(
        Instant timestamp,
        String correlationId,
        String service,
        String level,
        String message
) { }
