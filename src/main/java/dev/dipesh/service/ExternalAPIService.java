package dev.dipesh.service;

import dev.dipesh.DTO.ApiResponseDTO;

public interface ExternalAPIService {
    ApiResponseDTO postGenerateDescription(String urlString, String requestBody);
}
