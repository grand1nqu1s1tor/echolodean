package dev.dipesh.repository;

import dev.dipesh.entity.Song;
import dev.dipesh.entity.SongLike;
import dev.dipesh.entity.SongLikeId;
import dev.dipesh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongLikeRepository extends JpaRepository<SongLike, SongLikeId> {
    Optional<SongLike> findBySongAndUser(Song song, User user);

    boolean existsBySongAndUser(Song song, User user);
}
