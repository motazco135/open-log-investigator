package io.github.motazco135.investigator.domain.port;

import io.github.motazco135.investigator.domain.model.InvestigationResult;
import io.github.motazco135.investigator.domain.model.TimelineEvent;

import java.util.List;

public interface LlmClientPort {

    InvestigationResult investigate(String correlationId, String question, List<TimelineEvent> timeline);

}
