package dev.dipesh.entity;

import java.io.Serializable;
import java.util.Objects;

public class SongLikeId implements Serializable {
    private String song; // Corresponds to the name of the attribute in SongLike
    private String user; // Corresponds to the name of the attribute in SongLike

    // Default constructor
    public SongLikeId() {}

    // Constructor
    public SongLikeId(String song, String user) {
        this.song = song;
        this.user = user;
    }

    // Getters and setters omitted for brevity

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongLikeId that = (SongLikeId) o;
        return Objects.equals(song, that.song) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(song, user);
    }
}
