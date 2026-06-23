package io.github.motazco135.investigator.api;

import io.github.motazco135.investigator.application.usecase.InvestigationUseCase;
import io.github.motazco135.investigator.domain.model.InvestigationResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestigationController.class)
class InvestigationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvestigationUseCase investigationUseCase;

    @Test
    void shouldInvestigateTransaction() throws Exception {
        Mockito.when(investigationUseCase.investigate(any()))
                .thenReturn(new InvestigationResult(
                        "corr-123",
                        "The transaction contains error events and requires investigation.",
                        "core-banking-api",
                        "Core banking API timeout",
                        List.of("Core banking API timeout"),
                        List.of("Review logs around core-banking-api.")
                ));

        mockMvc.perform(post("/api/investigations")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "correlationId": "corr-123",
                                  "question": "Why did this fail?"
                                }
                                """))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correlationId", is("corr-123")))
                .andExpect(jsonPath("$.failurePoint", is("core-banking-api")))
                .andExpect(jsonPath("$.rootCause", is("Core banking API timeout")));
    }

    @Test
    void shouldRejectMissingCorrelationId() throws Exception {
        mockMvc.perform(post("/api/investigations")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "Why did this fail?"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}