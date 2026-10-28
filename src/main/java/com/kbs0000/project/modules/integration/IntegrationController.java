package com.kbs0000.project.modules.integration;

import com.ofxreader.dto.AccountStatement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/integrations")
@Validated
public class IntegrationController {
    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AccountStatement> mapOFXFile(
            @RequestParam("file") MultipartFile file
    ) {
        AccountStatement statement = integrationService.mapOFXFile(file);
        return ResponseEntity.ok(statement);
    }
}
