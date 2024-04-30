package dev.dipesh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SongResponseDTO {
    private int code;
    private String msg;
    private SongData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SongData {
        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("song_id")
        private String songId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("title")
        private String title;

        @JsonProperty("image_large_url")
        private String imageLargeUrl;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("model_name")
        private String modelName;

        @JsonProperty("video_url")
        private String videoUrl;

        @JsonProperty("audio_url")
        private String audioUrl;

        @JsonProperty("meta_tags")
        private String metaTags;

        @JsonProperty("meta_prompt")
        private String metaPrompt;

        @JsonProperty("meta_duration")
        private Integer metaDuration;

        @JsonProperty("meta_error_msg")
        private String metaErrorMsg;

        @JsonProperty("meta_error_type")
        private String metaErrorType;
    }
}
