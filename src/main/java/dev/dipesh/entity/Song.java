package dev.dipesh.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "songs_metadata")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Song {

    @Id
    @Column(name = "song_id")
    @JsonProperty("song_id")
    private String songId;

    @Column(nullable = false)
    @JsonIgnore
    private String userId;

    @Column(nullable = false)
    private String title;

    private String description;

    @JsonProperty("duration")
    private double duration;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonProperty("create_time")
    private Date createdAt;

    @JsonProperty("audio_url")
    private String audioUrl;

    @JsonProperty("image_url")
    private String imageUrl;

    @Lob
    @JsonProperty("meta_prompt")
    private String lyrics;
}
