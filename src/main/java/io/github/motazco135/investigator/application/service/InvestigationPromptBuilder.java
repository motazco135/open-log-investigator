package io.github.motazco135.investigator.application.service;

import io.github.motazco135.investigator.domain.model.InvestigationStatus;
import io.github.motazco135.investigator.domain.model.TimelineEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class InvestigationPromptBuilder {

    public String build(
            String correlationId,
            InvestigationStatus status,
            String question,
            List<TimelineEvent> timeline
    ) {
        return """
                You are an expert software and business transaction investigator.

                The transaction status has already been determined by deterministic rules.

                Status:
                %s
                
                Your job:
                - Explain the status using only the timeline.
                - Do not change the status.
                - Do not assume the transaction failed unless status is FAILED.
                - If status is INCONCLUSIVE, clearly say evidence is insufficient.
                - Return only valid JSON.
                - Do not wrap response in markdown.
                - If status is INCONCLUSIVE, set: failurePoint = "UNKNOWN" and rootCause = "Insufficient evidence"

                Required JSON:
                {
                  "correlationId": "%s",
                  "status": "%s",
                  "summary": "...",
                  "failurePoint": "...",
                  "rootCause": "...",
                  "evidence": ["..."],
                  "recommendations": ["..."]
                }

                User question:
                %s

                Timeline:
                %s
                """.formatted(
                status,
                correlationId,
                status,
                question == null || question.isBlank()
                        ? "What happened to this transaction?"
                        : question,
                formatTimeline(timeline)
        );
    }

    private String formatTimeline(List<TimelineEvent> timeline) {
        return timeline.stream()
                .map(event -> "- [%s] service=%s level=%s message=%s".formatted(
                        event.timestamp(),
                        event.service(),
                        event.level(),
                        event.description()
                ))
                .toList()
                .toString();
    }
}