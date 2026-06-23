package io.github.motazco135.investigator.api;

import io.github.motazco135.investigator.api.request.InvestigationApiRequest;
import io.github.motazco135.investigator.api.response.InvestigationApiResponse;
import io.github.motazco135.investigator.application.usecase.InvestigationUseCase;
import io.github.motazco135.investigator.domain.model.InvestigationRequest;
import io.github.motazco135.investigator.domain.model.InvestigationResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investigations")
public class InvestigationController {

    private final InvestigationUseCase investigationUseCase;

    public InvestigationController(InvestigationUseCase investigationUseCase) {
        this.investigationUseCase = investigationUseCase;
    }

    @PostMapping
    public ResponseEntity<InvestigationApiResponse> investigate(@Valid @RequestBody InvestigationApiRequest request) {
        var result = investigationUseCase.investigate(toDomain(request));
        return ResponseEntity.ok(toApiResponse(result));
    }

    private InvestigationRequest toDomain(InvestigationApiRequest request) {
        return new InvestigationRequest(request.correlationId(), request.question());
    }

    private InvestigationApiResponse toApiResponse(InvestigationResult result) {
        return new InvestigationApiResponse(
                result.correlationId(),
                result.summary(),
                result.failurePoint(),
                result.rootCause(),
                result.evidence(),
                result.recommendations()
        );
    }
}
