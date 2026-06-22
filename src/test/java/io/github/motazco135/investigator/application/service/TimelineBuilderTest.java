package io.github.motazco135.investigator.application.service;

import io.github.motazco135.investigator.domain.model.LogEntry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TimelineBuilderTest {
    private final TimelineBuilder timelineBuilder = new TimelineBuilder();

    @Test
    void shouldBuildOrderedTimelineFromLogs() {
        var logs = List.of(
                new LogEntry(
                        Instant.parse("2026-06-21T10:30:03Z"),
                        "corr-123",
                        "core-banking-api",
                        "ERROR",
                        "Core banking API timeout"
                ),
                new LogEntry(
                        Instant.parse("2026-06-21T10:30:00Z"),
                        "corr-123",
                        "payment-service",
                        "INFO",
                        "Payment request received"
                ),
                new LogEntry(
                        Instant.parse("2026-06-21T10:30:01Z"),
                        "corr-123",
                        "fraud-service",
                        "INFO",
                        "Fraud validation approved"
                )

        );

        var timeline = timelineBuilder.build(logs);
        assertThat(timeline).hasSize(3);
        assertThat(timeline)
                .extracting("service")
                .containsExactly(
                        "payment-service",
                        "fraud-service",
                        "core-banking-api"
                );

    }
}
