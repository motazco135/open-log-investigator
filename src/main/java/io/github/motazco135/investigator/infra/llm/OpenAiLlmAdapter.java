package io.github.motazco135.investigator.infra.llm;

import io.github.motazco135.investigator.application.service.InvestigationPromptBuilder;
import io.github.motazco135.investigator.domain.model.InvestigationResult;
import io.github.motazco135.investigator.domain.model.InvestigationStatus;
import io.github.motazco135.investigator.domain.model.TimelineEvent;
import io.github.motazco135.investigator.domain.port.LlmClientPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public final class OpenAiLlmAdapter implements LlmClientPort {

    private final ChatClient chatClient;
    private final JsonMapper jsonMapper;
    private final InvestigationPromptBuilder promptBuilder;

    public OpenAiLlmAdapter(ChatClient.Builder chatClientBuilder, JsonMapper jsonMapper, InvestigationPromptBuilder promptBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.jsonMapper = jsonMapper;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public InvestigationResult investigate(String correlationId, InvestigationStatus status, String question, List<TimelineEvent> timeline) {
        var prompt = promptBuilder.build(correlationId, status, question, timeline);
        var response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return toInvestigationResult(response);
    }

    private InvestigationResult toInvestigationResult(String response) {
        try {
            return jsonMapper.readValue(response, InvestigationResult.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to parse LLM response: " + response, exception);
        }
    }
}
