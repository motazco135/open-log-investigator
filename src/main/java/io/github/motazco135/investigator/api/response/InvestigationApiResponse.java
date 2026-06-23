package io.github.motazco135.investigator.api.response;

import java.util.List;

public record InvestigationApiResponse(
        String correlationId,
        String summary,
        String failurePoint,
        String rootCause,
        List<String> evidence,
        List<String> recommendations
) { }
