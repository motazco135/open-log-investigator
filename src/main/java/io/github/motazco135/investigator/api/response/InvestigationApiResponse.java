package io.github.motazco135.investigator.api.response;

import io.github.motazco135.investigator.domain.model.InvestigationStatus;

import java.util.List;

public record InvestigationApiResponse(
        String correlationId,
        InvestigationStatus status,
        String summary,
        String failurePoint,
        String rootCause,
        List<String> evidence,
        List<String> recommendations
) { }
