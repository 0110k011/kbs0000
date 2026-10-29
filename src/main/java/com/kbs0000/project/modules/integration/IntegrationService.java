package com.kbs0000.project.modules.integration;

import com.kbs0000.project.modules.integration.exception.IntegrationOFXException;
import com.ofxreader.OFXReader;
import com.ofxreader.dto.AccountStatement;
import com.ofxreader.exception.OFXException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class IntegrationService {

    public AccountStatement mapOFXFile(MultipartFile file) {
        try {
            OFXReader ofxReader = new OFXReader();
            return ofxReader.processOFX(file.getBytes());
        } catch (OFXException e) {
            throw new IntegrationOFXException(e.getMessage(), HttpStatus.valueOf(e.getStatus()), e);
        } catch (IOException e) {
            throw new IntegrationOFXException(e.getMessage());
        }
    }

}
