package io.github.motazco135.investigator.infra.logs;

import io.github.motazco135.investigator.domain.model.LogEntry;
import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@Component
public final class JsonLinesLogSourceAdapter implements LogSourcePort {

    private final JsonMapper jsonMapper;
    private final ResourceLoader resourceLoader;
    private final LogSourceProperties properties;

    public JsonLinesLogSourceAdapter(JsonMapper jsonMapper,
                                     ResourceLoader resourceLoader,
                                     LogSourceProperties properties) {
        this.jsonMapper = jsonMapper;
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    @Override
    public List<LogEntry> findByCorrelationId(String correlationId) {
        return readLogs().stream()
                .filter(log -> log.correlationId().equals(correlationId))
                .sorted(Comparator.comparing(LogEntry::timestamp))
                .toList();
    }

    private List<LogEntry> readLogs() {
        var resource = resourceLoader.getResource(properties.filePath());
        try (var inputStream = resource.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.isBlank()).map(this::toLogEntry).toList();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read log file: " + properties.filePath(), exception);
        }
    }

    private LogEntry toLogEntry(String line) {
        try {
            return jsonMapper.readValue(line, LogEntry.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid log entry: " + line, exception);
        }
    }
}
