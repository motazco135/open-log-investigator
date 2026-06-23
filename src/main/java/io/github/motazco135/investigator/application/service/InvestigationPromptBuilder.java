package io.github.motazco135.investigator.application.service;

import io.github.motazco135.investigator.domain.model.TimelineEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestigationPromptBuilder {

    public String build(String correlationId, String question, List<TimelineEvent> timeline) {
        return """
                You are an expert software and business transaction investigator.
                Analyze the transaction timeline below and return a clear investigation report.
                Rules:
                - Be concise.
                - Do not invent facts.
                - Use only the provided timeline.
                - If evidence is missing, say that evidence is missing.
                - Return only valid JSON.
                - Do not wrap the response in markdown.
                Required JSON structure:
                {
                  "correlationId": "%s",
                  "summary": "...",
                  "failurePoint": "...",
                  "rootCause": "...",
                  "evidence": ["..."],
                  "recommendations": ["..."]
                }
                User question:%s
                Timeline:%s
                """.formatted(
                correlationId,
                question == null || question.isBlank() ? "What happened to this transaction?" : question,
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
                )).toList().toString();
    }
}
