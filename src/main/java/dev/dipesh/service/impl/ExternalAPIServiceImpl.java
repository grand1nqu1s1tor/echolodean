package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.dto.ApiResponseDTO;
import dev.dipesh.dto.SongResponseDTO;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.util.ApiUrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${api.key}")
    private String apiKey;

    @Autowired
    public ExternalAPIServiceImpl(HttpClient client) {
        this.client = client;
    }

    @Override
    public ApiResponseDTO postGenerateDescription(String urlString, String requestBody) {
        HttpRequest request = buildPostRequest(urlString, requestBody);
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ApiResponseDTO.parseResponse(response.body());
        } catch (Exception e) {
            return new ApiResponseDTO(null, false, "Error communicating with the third-party service: " + e.getMessage());
        }
    }

    public CompletableFuture<Boolean> checkForCompletion(String songId) {
        String url = ApiUrlConstants.GET_SONG + songId;
        HttpRequest request = buildGetRequest(url);

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseCompletionResponse);
    }

    private boolean parseCompletionResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            if (rootNode.path("code").asInt() == 0 && "complete".equals(rootNode.path("data").path("status").asText())) {
                return !rootNode.path("data").path("audio_url").asText("").isEmpty() &&
                        !rootNode.path("data").path("image_url").asText("").isEmpty();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CompletableFuture<SongResponseDTO> getSongDetails(String songId) {
        String url = ApiUrlConstants.GET_SONG + songId;
        HttpRequest request = buildGetRequest(url);

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseSongDetails);
    }

    private SongResponseDTO parseSongDetails(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, SongResponseDTO.class);
        } catch (Exception e) {
            SongResponseDTO errorDto = new SongResponseDTO();
            errorDto.setMsg("Parsing error: " + e.getMessage());
            return errorDto;
        }
    }

    private HttpRequest buildPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .build();
    }

    private HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .build();
    }
}
