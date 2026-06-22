package io.github.motazco135.investigator.infra.logs;


import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "investigator.logs")
public record LogSourceProperties(@NotBlank String filePath) {
}
