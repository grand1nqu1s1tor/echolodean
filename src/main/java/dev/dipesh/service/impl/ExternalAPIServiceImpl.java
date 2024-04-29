package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.DTO.ApiResponseDTO;
import dev.dipesh.DTO.SongResponseDTO;
import dev.dipesh.service.ExternalAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

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


    public CompletableFuture<Boolean> checkForCompletion(String songId) {
        try {
            // Construct the URL with the song ID
            String url = "https://api.sunoaiapi.com/api/v1/gateway/feed/" + songId;

            // Create a GET HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .header("Accept", "application/json") // Assuming the response is in JSON format.
                    .header("api-key", apiKey) // Ensure apiKey is defined in your class
                    .build();

            // Send the request asynchronously
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body) // Extract body when response is received
                    .thenApply(this::parseResponse); // Process the body to check completion
        } catch (URISyntaxException e) {
            CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }



    private boolean parseResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            if (rootNode.path("code").asInt() == 0) {
                JsonNode dataNode = rootNode.path("data");

                if ("complete".equals(dataNode.path("status").asText())) {
                    String audioUrl = dataNode.path("audio_url").asText(null);
                    String imageUrl = dataNode.path("image_url").asText(null);

                    // Check if both URLs are not null or empty
                    return audioUrl != null && !audioUrl.isEmpty() && imageUrl != null && !imageUrl.isEmpty();
                }
            }
        } catch (IOException e) {
            // Log error
            return false;
        }
        return false;
    }


    public CompletableFuture<SongResponseDTO> getSongDetails(String songId) {
        String url = "https://api.sunoaiapi.com/api/v1/gateway/feed/" + songId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseSongDetails);
    }

    private SongResponseDTO parseSongDetails(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, SongResponseDTO.class);
        } catch (Exception e) {
            // Log error and handle exception, perhaps return a default or error instance
            SongResponseDTO errorDto = new SongResponseDTO();
            errorDto.setMsg("Parsing error: " + e.getMessage());
            return errorDto;
        }
    }

}
