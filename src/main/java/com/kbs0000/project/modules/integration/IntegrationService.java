package com.kbs0000.project.modules.integration;

import com.kbs0000.project.modules.integration.exception.OFXException;
import com.ofxreader.OFXReader;
import com.ofxreader.dto.AccountStatement;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class IntegrationService {
    public AccountStatement mapOFXFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            OFXReader ofxReader = new OFXReader();
            return ofxReader.processOFX(inputStream);
        } catch (Exception e) {
            throw new OFXException(e.getMessage(), e);
        }
    }
}
