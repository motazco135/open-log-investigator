package io.github.motazco135.investigator.application.usecase;

import io.github.motazco135.investigator.application.service.TimelineBuilder;
import io.github.motazco135.investigator.domain.model.InvestigationRequest;
import io.github.motazco135.investigator.domain.model.LogEntry;
import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvestigationUseCaseTest {

    @Test
    void shouldReturnFailurePointFromFirstErrorLog() {
        var logSourcePort = new FakeLogSourcePort(List.of(
                new LogEntry(
                        Instant.parse("2026-06-21T10:30:00Z"),
                        "corr-123",
                        "payment-service",
                        "INFO",
                        "Payment request received"
                ),

                new LogEntry(
                        Instant.parse("2026-06-21T10:30:03Z"),
                        "corr-123",
                        "core-banking-api",
                        "ERROR",
                        "Core banking API timeout while posting transaction"
                )
        ));

        var useCase = new InvestigationUseCase(new TimelineBuilder(),logSourcePort);

        var result = useCase.investigate(
                new InvestigationRequest("corr-123", "Why did this fail?")
        );
        assertThat(result.correlationId()).isEqualTo("corr-123");
        assertThat(result.failurePoint()).isEqualTo("core-banking-api");
        assertThat(result.rootCause()).isEqualTo("Core banking API timeout while posting transaction");
    }

    @Test
    void shouldReturnNoLogsFoundWhenCorrelationIdDoesNotExist() {
        var useCase = new InvestigationUseCase( new TimelineBuilder(),new FakeLogSourcePort(List.of()));
        var result = useCase.investigate(
                new InvestigationRequest("missing-id", "Why did this fail?")
        );
        assertThat(result.failurePoint()).isEqualTo("UNKNOWN");
        assertThat(result.rootCause()).isEqualTo("No evidence available");
    }

    private record FakeLogSourcePort(List<LogEntry> logs) implements LogSourcePort {
        @Override
        public List<LogEntry> findByCorrelationId(String correlationId) {
            return logs.stream()
                    .filter(log -> log.correlationId().equals(correlationId))
                    .toList();
        }

    }
}