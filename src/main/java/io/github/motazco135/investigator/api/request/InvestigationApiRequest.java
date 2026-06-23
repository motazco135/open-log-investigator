package io.github.motazco135.investigator.api.request;

import jakarta.validation.constraints.NotBlank;

public record InvestigationApiRequest(
        @NotBlank(message = "correlationId is required")
        String correlationId,
        String question
) {
}
