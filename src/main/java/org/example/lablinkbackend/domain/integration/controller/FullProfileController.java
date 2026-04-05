package org.example.lablinkbackend.domain.integration.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lablinkbackend.domain.integration.model.dto.FullProfileResponseDto;
import org.example.lablinkbackend.domain.integration.service.IntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class FullProfileController {

    private final IntegrationService integrationService;

    @GetMapping("/full-profile/{orcid}")
    public ResponseEntity<FullProfileResponseDto> getFullProfile(@PathVariable String orcid) {
        log.info("Received request for full profile with ORCID: {}", orcid);

        FullProfileResponseDto profile = integrationService.getFullProfile(orcid);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }
}