package dev.dipesh.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO {
    private String body;
    private boolean successful;
    private String errorMessage;

    public static ApiResponseDTO parseResponse(String body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            if(rootNode.isObject()){
                String message = rootNode.path("detail").asText();
                if (message.contains("Insufficient credits") || message.contains("No remaining songs")) {
                    return new ApiResponseDTO(body, false, message);
                }
            }
            return new ApiResponseDTO(body, true, null);
        } catch (IOException e) {
            return new ApiResponseDTO(null, false, "Failed to parse response: " + e.getMessage());
        }
    }

}