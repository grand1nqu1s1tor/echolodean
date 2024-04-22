package dev.dipesh.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PromptRequestDTO {
    private String gptDescriptionPrompt;
    private boolean makeInstrumental;
    private String mv;
    private String prompt;

    // Getters and setters
}


