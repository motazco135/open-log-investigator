package io.github.motazco135.investigator.api.response;

import io.github.motazco135.investigator.domain.model.InvestigationStatus;
import io.github.motazco135.investigator.domain.model.TimelineEvent;

import java.util.List;

public record InvestigationApiResponse(
        String correlationId,
        InvestigationStatus status,
        String summary,
        String failurePoint,
        String rootCause,
        List<String> evidence,
        List<String> recommendations,
        List<TimelineEvent> timeline
) { }
