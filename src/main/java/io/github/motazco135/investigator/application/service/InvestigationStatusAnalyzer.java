package io.github.motazco135.investigator.application.service;



import io.github.motazco135.investigator.domain.model.InvestigationStatus;

import io.github.motazco135.investigator.domain.model.TimelineEvent;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

public final class InvestigationStatusAnalyzer {

    public InvestigationStatus analyze(List<TimelineEvent> timeline) {
        if (timeline.isEmpty()) {
            return InvestigationStatus.NO_EVIDENCE;
        }

        if (containsAny(timeline, "ERROR", "FAILED", "FAILURE", "TIMEOUT", "EXCEPTION", "REJECTED")) {
            return InvestigationStatus.FAILED;
        }

        if (containsAny(timeline, "SUCCESS", "SUCCESSFUL", "COMPLETED", "APPROVED", "POSTED")) {
            return InvestigationStatus.SUCCESSFUL;
        }
        return InvestigationStatus.INCONCLUSIVE;
    }

    private boolean containsAny(List<TimelineEvent> timeline, String... keywords) {
        return timeline.stream()
                .map(this::searchableText)
                .anyMatch(text -> containsKeyword(text, keywords));
    }

    private String searchableText(TimelineEvent event) {
        return "%s %s %s".formatted(
                event.level(),
                event.service(),
                event.description()
        ).toUpperCase();
    }

    private boolean containsKeyword(String text, String[] keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

}
