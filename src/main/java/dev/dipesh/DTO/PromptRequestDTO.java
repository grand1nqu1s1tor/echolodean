package dev.dipesh.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PromptRequestDTO {
    private String gpt_description_prompt;
    private boolean make_instrumental;
    private String mv;
    private String prompt;

    // Getters and setters
}


