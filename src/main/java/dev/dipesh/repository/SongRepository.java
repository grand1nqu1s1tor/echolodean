package dev.dipesh.repository;

import dev.dipesh.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findByUserId(String userId);

    @Query(value = "SELECT s.*, COUNT(l.user_id) as likes \n" +
            "FROM songs_metadata s \n" +
            "LEFT JOIN song_likes l ON s.song_id = l.song_id \n" +
            "GROUP BY s.song_id \n" +
            "ORDER BY likes DESC\n", nativeQuery = true)
    Page<Song> findTopTrendingSongs(Pageable pageable);


    @Query(value = "SELECT s.* FROM songs_metadata s JOIN song_likes ul ON s.song_id = ul.song_id WHERE ul.user_id = :userId", nativeQuery = true)
    List<Song> findLikedSongsByUserId(@Param("userId") String userId);
}
