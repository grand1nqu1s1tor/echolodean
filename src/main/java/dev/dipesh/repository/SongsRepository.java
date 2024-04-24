package dev.dipesh.repository;

import dev.dipesh.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongsRepository extends JpaRepository<Song, String> {
    List<Song> findByUserId(String userId);
}


