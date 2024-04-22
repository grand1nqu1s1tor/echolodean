package dev.dipesh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "songs_metadata")
@Getter
@Setter
public class Song {
    @Id
    private String songId;  // Assuming this is a UUID generated elsewhere

    @Column(nullable = false)
    private String userId;  // Holds the user ID as a string without establishing a relationship

    @Column(nullable = false)
    private String title;

    private String description;

    private double duration;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    private int likes;

    @Column(nullable = true)
    private String audioUrl;  // URL to the audio file

    @Column(nullable = true)
    private String imageUrl;  // URL to the image file

    @Lob  // Use Lob for potentially large text data
    private String lyrics;  // Text of the song's lyrics
}
