package dev.dipesh.handler;

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
public class ApiResponse {
    private String body;
    private boolean successful;
    private String errorMessage;

    // Custom method to parse and check for specific error messages in the response body
    public static ApiResponse parseResponse(String body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            if(rootNode.isObject()){
                String message = rootNode.path("detail").asText();
                if (message.contains("Insufficient credits")) {
                    return new ApiResponse(body, false, message);
                }
            }
            return new ApiResponse(body, true, null);
        } catch (IOException e) {
            // If there's an exception in parsing, assume it's a parse failure not a content error
            return new ApiResponse(null, false, "Failed to parse response: " + e.getMessage());
        }
    }

}