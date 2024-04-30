package dev.dipesh.service;

import dev.dipesh.dto.ApiResponseDTO;
import dev.dipesh.dto.SongResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface ExternalAPIService {
    ApiResponseDTO postGenerateDescription(String urlString, String requestBody);

    CompletableFuture<Boolean> checkForCompletion(String songId);

    CompletableFuture<SongResponseDTO> getSongDetails(String songId);

}
