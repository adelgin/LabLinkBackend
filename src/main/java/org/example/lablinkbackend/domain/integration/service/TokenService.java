package org.example.lablinkbackend.domain.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class TokenService {

    @Value("${keycloak.token-url:http://localhost:8085/realms/lablink/protocol/openid-connect/token}")
    private String tokenUrl;

    @Value("${keycloak.client-id:lablink-client}")
    private String clientId;

    @Value("${keycloak.client-secret:duHha0JSHi8ETMVIXpvLwM9nLhc8BgXP}")
    private String clientSecret;

    private final RestClient restClient;
    private String cachedToken;
    private Instant tokenExpiry;

    public TokenService() {
        this.restClient = RestClient.builder().build();
    }

    public synchronized String getServiceToken() {
        if (cachedToken != null && tokenExpiry != null
                && Instant.now().isBefore(tokenExpiry.minusSeconds(60))) {
            return cachedToken;
        }

        log.info("Requesting new service token from Keycloak");

        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            cachedToken = (String) response.get("access_token");
            Integer expiresIn = (Integer) response.get("expires_in");

            if (expiresIn != null) {
                tokenExpiry = Instant.now().plusSeconds(expiresIn);
            }

            log.info("Successfully obtained service token");
            return cachedToken;

        } catch (Exception e) {
            log.error("Failed to obtain service token", e);
            throw new RuntimeException("Failed to obtain service token", e);
        }
    }
}