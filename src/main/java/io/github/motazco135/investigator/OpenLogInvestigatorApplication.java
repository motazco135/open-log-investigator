package io.github.motazco135.investigator;

import io.github.motazco135.investigator.infra.logs.LogSourceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LogSourceProperties.class)
public class OpenLogInvestigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenLogInvestigatorApplication.class, args);
    }

}
