package dev.dipesh.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class SongLikeId implements Serializable {
    private String user;
    private String song;

    public SongLikeId() {
    }

    public SongLikeId(String userId, String songId) {
        this.user = user;
        this.song = song;
    }

}

