package org.example.lablinkbackend.domain.integration.client;

import org.example.lablinkbackend.domain.integration.model.dto.FullProfileResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1/integration")
public interface IntegrationApiClient {

    @GetExchange("/full-profile/{orcid}")
    FullProfileResponseDto getFullProfile(@PathVariable("orcid") String orcid);
}
