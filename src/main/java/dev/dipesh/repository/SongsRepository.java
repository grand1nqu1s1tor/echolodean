package dev.dipesh.repository;

import dev.dipesh.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongsRepository extends JpaRepository<Song, String> {
}


