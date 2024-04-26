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
@JsonIgnoreProperties(ignoreUnknown = true) // This tells Jackson to ignore fields not listed here
@ToString
public class Song {

    @Id
    @JsonProperty("song_id") // Match JSON property to the Java field
    private String songId;

    @Column(nullable = false)
    @JsonIgnore
    private String userId;

    @Column(nullable = false)
    private String title;

    private String description;

    @JsonProperty("duration") // Duration in JSON might be in different units, ensure proper conversion if needed
    private double duration;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @JsonProperty("create_time") // Match JSON property to the Java field
    private Date createdAt;

    @JsonProperty("audio_url") // Match JSON property to the Java field
    private String audioUrl;

    @JsonProperty("image_url") // Match JSON property to the Java field
    private String imageUrl;

    @Lob
    @JsonProperty("meta_prompt") // Assuming this is where lyrics are stored in JSON
    private String lyrics;
}
