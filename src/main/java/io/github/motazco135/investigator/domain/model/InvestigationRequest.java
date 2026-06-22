package io.github.motazco135.investigator.domain.model;

public record InvestigationRequest(
        String correlationId,
        String question
) { }
