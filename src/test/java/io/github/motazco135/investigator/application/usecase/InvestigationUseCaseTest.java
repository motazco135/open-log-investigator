package io.github.motazco135.investigator.application.usecase;

import io.github.motazco135.investigator.application.service.InvestigationStatusAnalyzer;
import io.github.motazco135.investigator.application.service.TimelineBuilder;
import io.github.motazco135.investigator.domain.model.*;
import io.github.motazco135.investigator.domain.port.LlmClientPort;
import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvestigationUseCaseTest {

    @Test
    void shouldReturnLlmInvestigationResult() {
        var useCase = new InvestigationUseCase(
                new FakeLogSourcePort(sampleLogs()),
                new TimelineBuilder(),
                new InvestigationStatusAnalyzer(),
                new FakeLlmClientPort()
        );

        var result = useCase.investigate(
                new InvestigationRequest("corr-123", "Why did this fail?")
        );

        assertThat(result.failurePoint()).isEqualTo("core-banking-api");
        assertThat(result.rootCause()).isEqualTo("Core banking API timeout");
    }

    @Test
    void shouldReturnNoLogsFoundWhenCorrelationIdDoesNotExist() {
        var useCase = new InvestigationUseCase(
                new FakeLogSourcePort(sampleLogs()),
                new TimelineBuilder(),
                new InvestigationStatusAnalyzer(),
                new FakeLlmClientPort()
        );

        var result = useCase.investigate(
                new InvestigationRequest("missing-id", "Why did this fail?")
        );

        assertThat(result.failurePoint()).isEqualTo("UNKNOWN");
        assertThat(result.rootCause()).isEqualTo("No evidence available");
    }

    @Test
    void shouldFallbackWhenLlmFails() {
        var useCase = new InvestigationUseCase(
                new FakeLogSourcePort(sampleLogs()),
                new TimelineBuilder(),
                new InvestigationStatusAnalyzer(),
                new FailingLlmClientPort()
        );

        var result = useCase.investigate(
                new InvestigationRequest("corr-123", "Why did this fail?")
        );

        assertThat(result.failurePoint()).isEqualTo("core-banking-api");
        assertThat(result.rootCause()).isEqualTo("Core banking API timeout while posting transaction");
    }

    private List<LogEntry> sampleLogs() {
        return List.of(
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
        );
    }

    private record FakeLogSourcePort(List<LogEntry> logs) implements LogSourcePort {
        @Override
        public List<LogEntry> findByCorrelationId(String correlationId) {
            return logs.stream()
                    .filter(log -> log.correlationId().equals(correlationId))
                    .toList();
        }
    }

    private static final class FakeLlmClientPort implements LlmClientPort {

        @Override
        public InvestigationResult investigate(
                String correlationId,
                InvestigationStatus status,
                String question,
                List<TimelineEvent> timeline
        ) {
            return new InvestigationResult(
                    correlationId,
                    InvestigationStatus.FAILED,
                    "The transaction failed during core banking posting.",
                    "core-banking-api",
                    "Core banking API timeout",
                    List.of("Core banking API timeout while posting transaction"),
                    List.of("Check core banking API availability."),
                    timeline
            );
        }
    }

    private static final class FailingLlmClientPort implements LlmClientPort {
        @Override
        public InvestigationResult investigate(
                String correlationId,
                InvestigationStatus status,
                String question,
                List<TimelineEvent> timeline
        ) {
            throw new IllegalStateException("LLM unavailable");
        }
    }
}