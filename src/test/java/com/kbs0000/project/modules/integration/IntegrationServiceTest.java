package com.kbs0000.project.modules.integration;

import com.kbs0000.project.modules.integration.exception.OFXException;
import com.ofxreader.dto.AccountStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntegrationServiceTest {

    @InjectMocks
    private IntegrationService integrationService;

    @Test
    void shouldMapOFXFileSuccessfully() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);

        String validOfxContent = "OFXHEADER:100\nDATA:OFX<OFX><DTSTART>20250101000000[-3:BRT]</DTSTART>\n" +
                "<DTEND>20250131000000[-3:BRT]</DTEND></OFX> \n";
        InputStream inputStream = new ByteArrayInputStream(validOfxContent.getBytes(StandardCharsets.UTF_8));

        when(mockFile.getInputStream()).thenReturn(inputStream);

        AccountStatement result = integrationService.mapOFXFile(mockFile);

        assertNotNull(result);
    }

    @Test
    void shouldThrowOFXExceptionWhenGetInputStreamFails() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getInputStream()).thenThrow(new IOException("Erro de leitura"));

        OFXException exception = assertThrows(OFXException.class, () -> {
            integrationService.mapOFXFile(mockFile);
        });

        assertEquals("Erro de leitura", exception.getMessage());
    }

    @Test
    void shouldThrowOFXExceptionWhenProcessOFXFails() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);

        String validOfxContent = "OFXHEADER:100\nDATA:OFX<OFX><DTSTART>20250101000000[-3:BRT]</DTSTART></OFX> \n";
        InputStream inputStream = new ByteArrayInputStream(validOfxContent.getBytes(StandardCharsets.UTF_8));

        when(mockFile.getInputStream()).thenReturn(inputStream);


        OFXException exception = assertThrows(OFXException.class, () -> {
            integrationService.mapOFXFile(mockFile);
        });
        assertEquals("DTEND não pode ser nulo", exception.getMessage());
    }
}
