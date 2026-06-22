package io.github.motazco135.investigator.infra.logs;

import io.github.motazco135.investigator.domain.port.LogSourcePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonLinesLogSourceAdapterTest {

    @Autowired
    private LogSourcePort logSourcePort;

    @Test
    void shouldFindLogsByCorrelationId() {
        var logs = logSourcePort.findByCorrelationId("corr-123");
        assertThat(logs).hasSize(4);
        assertThat(logs)
                .extracting("service")
                .containsExactly("payment-service",
                        "fraud-service",
                        "core-banking-api",
                        "payment-service");
    }

    @Test
    void shouldReturnEmptyListWhenCorrelationIdDoesNotExist() {
        var logs = logSourcePort.findByCorrelationId("missing-correlation-id");
        assertThat(logs).isEmpty();
    }

}
