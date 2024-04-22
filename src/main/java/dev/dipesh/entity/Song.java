package dev.dipesh.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "songs_metadata")
@Getter
@Setter
public class SongsMetadata {
    @Id
    private String songId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    private String description;

    private double duration;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    private int likes;

}
