package io.github.motazco135.investigator.application.usecase;

import io.github.motazco135.investigator.application.service.TimelineBuilder;
import io.github.motazco135.investigator.domain.model.InvestigationRequest;
import io.github.motazco135.investigator.domain.model.InvestigationResult;
import io.github.motazco135.investigator.domain.model.TimelineEvent;
import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestigationUseCase {

    private final TimelineBuilder timelineBuilder;
    private final LogSourcePort logSourcePort;

    public InvestigationUseCase(TimelineBuilder timelineBuilder, LogSourcePort logSourcePort) {
        this.timelineBuilder = timelineBuilder;
        this.logSourcePort = logSourcePort;
    }

    public InvestigationResult investigate(InvestigationRequest request) {
        var logs = logSourcePort.findByCorrelationId(request.correlationId());
        var timeline = timelineBuilder.build(logs);
        if (timeline.isEmpty()) {
            return noLogsFound(request.correlationId());
        }
        return buildPreliminaryResult(request.correlationId(), timeline);
    }

    private InvestigationResult noLogsFound(String correlationId) {
        return new InvestigationResult(
                correlationId,
                "No logs were found for the provided correlation ID.",
                "UNKNOWN",
                "No evidence available",
                List.of(),
                List.of("Verify that the correlation ID is correct.")
        );
    }

    private InvestigationResult buildPreliminaryResult(String correlationId, List<TimelineEvent> timeline) {
        var errorEvents = timeline.stream()
                .filter(event -> "ERROR".equalsIgnoreCase(event.level()))
                .toList();
        if (errorEvents.isEmpty()) {
            return new InvestigationResult(
                    correlationId,
                    "The transaction completed without detected error logs.",
                    "NONE",
                    "No error events found",
                    timeline.stream().map(TimelineEvent::description).toList(),
                    List.of("Review the full timeline if business status is still unclear.")
            );
        }
        var firstError = errorEvents.getFirst();
        return new InvestigationResult(
                correlationId,
                "The transaction contains error events and requires investigation.",
                firstError.service(),
                firstError.description(),
                errorEvents.stream().map(TimelineEvent::description).toList(),
                List.of("Review logs around " + firstError.service() + ".")
        );
    }
}
