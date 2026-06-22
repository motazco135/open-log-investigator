package io.github.motazco135.investigator.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

public class LogEntryTest {
    @Test
    void shouldCreateLogEntry() {
        var timestamp = Instant.parse("2026-06-21T10:30:00Z");
        var logEntry = new LogEntry(
                timestamp,
                "corr-123",
                "payment-service",
                "INFO",
                "Payment request received"
        );
        assertThat(logEntry.timestamp()).isEqualTo(timestamp);
        assertThat(logEntry.correlationId()).isEqualTo("corr-123");
        assertThat(logEntry.service()).isEqualTo("payment-service");
        assertThat(logEntry.level()).isEqualTo("INFO");
        assertThat(logEntry.message()).isEqualTo("Payment request received");
    }
}
