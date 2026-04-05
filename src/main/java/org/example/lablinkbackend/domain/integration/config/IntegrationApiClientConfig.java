package org.example.lablinkbackend.domain.integration.config;

import org.example.lablinkbackend.domain.integration.client.IntegrationApiClient;
import org.example.lablinkbackend.domain.integration.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;

@Configuration
public class IntegrationApiClientConfig {

    @Value("${integration.api.base-url:http://localhost:8080}")
    private String baseUrl;

    private final TokenService tokenService;

    public IntegrationApiClientConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public IntegrationApiClient integrationApiClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .requestInterceptor(new BearerTokenInterceptor(tokenService))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(IntegrationApiClient.class);
    }

    private static class BearerTokenInterceptor implements ClientHttpRequestInterceptor {
        private final TokenService tokenService;

        public BearerTokenInterceptor(TokenService tokenService) {
            this.tokenService = tokenService;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            String token = tokenService.getServiceToken();
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        }
    }
}