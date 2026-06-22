package io.github.motazco135.investigator.application.service;

import io.github.motazco135.investigator.domain.model.LogEntry;
import io.github.motazco135.investigator.domain.model.TimelineEvent;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public final class TimelineBuilder {

    public List<TimelineEvent> build(List<LogEntry> logs) {
        return logs.stream()
                .sorted(Comparator.comparing(LogEntry::timestamp))
                .map(this::toTimelineEvent)
                .toList();
    }

    private TimelineEvent toTimelineEvent(LogEntry logEntry) {
        return new TimelineEvent(
                logEntry.timestamp(),
                logEntry.service(),
                logEntry.level(),
                logEntry.message()
        );
    }

}
