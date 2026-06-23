package io.github.motazco135.investigator.domain.model;

import java.util.List;

public record InvestigationResult(
        String correlationId,
        InvestigationStatus status,
        String summary,
        String failurePoint,
        String rootCause,
        List<String> evidence,
        List<String> recommendations
) {
}
