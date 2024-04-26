package dev.dipesh.repository;

import dev.dipesh.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findByUserId(String userId);

    @Query("SELECT sm FROM Song sm " +
            "LEFT JOIN SongLike sl ON sm.songId = sl.song.songId " +
            "GROUP BY sm.songId " +
            "ORDER BY COUNT(sl.liked) DESC")
    List<Song> findTopTrendingSongs(Pageable pageable);
}


