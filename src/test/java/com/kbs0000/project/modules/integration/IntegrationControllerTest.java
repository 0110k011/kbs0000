package com.kbs0000.project.modules.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs0000.project.modules.integration.exception.IntegrationOFXException;
import com.ofxreader.dto.AccountStatement;
import com.ofxreader.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntegrationController.class)
public class IntegrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IntegrationService integrationService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private Transaction createValidTransaction() {
        RandomGenerator random = RandomGenerator.of("Xoshiro256PlusPlus");
        return new Transaction(
                String.valueOf(random.nextInt(0, 999)),
                new BigDecimal("-50.00"),
                LocalDateTime.now(),
                "Store Purchase" + random.nextInt(0, 999),
                "DEBIT"
        );
    }

    private AccountStatement createValidAccountStatement() {
        return new AccountStatement(
                "BANK S.A.",
                "BRL",
                "0000",
                "1",
                "00000000-0",
                "CHECKING",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(createValidTransaction()),
                0
        );
    }

    @Test
    void shouldReturn200WhenUploadOfxFileSuccessfully() throws Exception {
        String ofxContent = "OFXHEADER:100\nDATA:OFXSGML\n<OFX></OFX>";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ofx",
                "application/octet-stream",
                ofxContent.getBytes()
        );

        when(integrationService.mapOFXFile(any())).thenReturn(createValidAccountStatement());

        mockMvc.perform(multipart("/api/v1/integrations")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenUploadOfxFileWithInvalidContent() throws Exception {
        String invalidOfxContent = "<HTML></HTML>";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ofx",
                "application/octet-stream",
                invalidOfxContent.getBytes()
        );

        when(integrationService.mapOFXFile(any())).thenThrow(new IntegrationOFXException("Tag <OFX> não encontrada no arquivo."));

        mockMvc.perform(multipart("/api/v1/integrations")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Tag <OFX> não encontrada no arquivo.")));
    }
}
