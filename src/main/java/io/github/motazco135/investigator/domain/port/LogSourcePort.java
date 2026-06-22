package io.github.motazco135.investigator.domain.port;

import io.github.motazco135.investigator.domain.model.LogEntry;

import java.util.List;

public interface LogSourcePort {

    List<LogEntry> findByCorrelationId(String correlationId);
}
