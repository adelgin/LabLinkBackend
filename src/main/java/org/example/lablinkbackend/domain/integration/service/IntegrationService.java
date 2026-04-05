package org.example.lablinkbackend.domain.integration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lablinkbackend.domain.integration.client.IntegrationApiClient;
import org.example.lablinkbackend.domain.integration.model.dto.FullProfileResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final IntegrationApiClient integrationApiClient;

    public FullProfileResponseDto getFullProfile(String orcid) {
        try {
            log.info("Fetching full profile for ORCID: {}", orcid);
            FullProfileResponseDto response = integrationApiClient.getFullProfile(orcid);
            log.info("Successfully fetched profile for ORCID: {}", orcid);
            return response;
        } catch (RestClientException e) {
            log.error("Failed to fetch profile for ORCID: {}", orcid, e);
            throw new RuntimeException("Failed to fetch profile from integration service", e);
        }
    }
}
