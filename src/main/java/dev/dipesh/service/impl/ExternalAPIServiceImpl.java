package dev.dipesh.service.impl;

import dev.dipesh.DTO.ApiResponseDTO;
import dev.dipesh.service.ExternalAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
    private final HttpClient client;

    @Autowired
    public ExternalAPIServiceImpl(HttpClient client) {
        this.client = client;
    }

    private final String apiKey = "4mANlXLpmBdJOqVkeYxjgWDvkF0G6gz+";

    @Override
    public ApiResponseDTO postGenerateDescription(String urlString, String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(URI.create(urlString))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("api-key", apiKey)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ApiResponseDTO.parseResponse(response.body());
        } catch (Exception e) {
            return new ApiResponseDTO(null, false, "Error communicating with the third-party service: " + e.getMessage());
        }
    }
}
