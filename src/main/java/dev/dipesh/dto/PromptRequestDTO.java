package dev.dipesh.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PromptRequestDTO {
    private String gpt_description_prompt;
    private String mv;
    private boolean make_instrumental;
    private String prompt;

}


