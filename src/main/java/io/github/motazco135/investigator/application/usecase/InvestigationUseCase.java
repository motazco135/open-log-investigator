package io.github.motazco135.investigator.application.usecase;

import io.github.motazco135.investigator.application.service.InvestigationStatusAnalyzer;
import io.github.motazco135.investigator.application.service.TimelineBuilder;
import io.github.motazco135.investigator.domain.model.InvestigationRequest;
import io.github.motazco135.investigator.domain.model.InvestigationResult;
import io.github.motazco135.investigator.domain.model.InvestigationStatus;
import io.github.motazco135.investigator.domain.model.TimelineEvent;
import io.github.motazco135.investigator.domain.port.LlmClientPort;
import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class InvestigationUseCase {

    private final LogSourcePort logSourcePort;
    private final TimelineBuilder timelineBuilder;
    private final InvestigationStatusAnalyzer statusAnalyzer;
    private final LlmClientPort llmClientPort;

    public InvestigationUseCase(
            LogSourcePort logSourcePort,
            TimelineBuilder timelineBuilder,
            InvestigationStatusAnalyzer statusAnalyzer,
            LlmClientPort llmClientPort
    ) {
        this.logSourcePort = logSourcePort;
        this.timelineBuilder = timelineBuilder;
        this.statusAnalyzer = statusAnalyzer;
        this.llmClientPort = llmClientPort;
    }

    public InvestigationResult investigate(InvestigationRequest request) {
        var logs = logSourcePort.findByCorrelationId(request.correlationId());
        var timeline = timelineBuilder.build(logs);
        var status = statusAnalyzer.analyze(timeline);

        if (status == InvestigationStatus.NO_EVIDENCE) {
            return noEvidence(request.correlationId());
        }

        try {
            return llmClientPort.investigate(
                    request.correlationId(),
                    status,
                    request.question(),
                    timeline
            );
        } catch (Exception exception) {
            return fallbackResult(request.correlationId(), status, timeline);
        }
    }

    private InvestigationResult noEvidence(String correlationId) {
        return new InvestigationResult(
                correlationId,
                InvestigationStatus.NO_EVIDENCE,
                "No logs were found for the provided correlation ID.",
                "UNKNOWN",
                "No evidence available",
                List.of(),
                List.of("Verify that the correlation ID is correct.")
        );
    }

    private InvestigationResult fallbackResult(
            String correlationId,
            InvestigationStatus status,
            List<TimelineEvent> timeline
    ) {
        var errorEvents = timeline.stream()
                .filter(event -> "ERROR".equalsIgnoreCase(event.level()))
                .toList();

        if (status == InvestigationStatus.INCONCLUSIVE) {
            return new InvestigationResult(
                    correlationId,
                    status,
                    "LLM analysis was unavailable. The available logs are insufficient to determine final transaction status.",
                    "UNKNOWN",
                    "Insufficient evidence",
                    timeline.stream().map(TimelineEvent::description).toList(),
                    List.of("Search for additional logs using transaction ID, trace ID, or downstream reference.")
            );
        }

        if (errorEvents.isEmpty()) {
            return new InvestigationResult(
                    correlationId,
                    status,
                    "LLM analysis was unavailable. No error events were found.",
                    "NONE",
                    "No error events found",
                    timeline.stream().map(TimelineEvent::description).toList(),
                    List.of("Review the full timeline if business status is still unclear.")
            );
        }

        var firstError = errorEvents.getFirst();

        return new InvestigationResult(
                correlationId,
                status,
                "LLM analysis was unavailable. Rule-based fallback detected error events.",
                firstError.service(),
                firstError.description(),
                errorEvents.stream().map(TimelineEvent::description).toList(),
                List.of("Review logs around " + firstError.service() + ".")
        );
    }
}