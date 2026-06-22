package io.github.motazco135.investigator.domain.model;

import java.time.Instant;

public record TimelineEvent(
        Instant timestamp,
        String service,
        String level,
        String description
) { }
