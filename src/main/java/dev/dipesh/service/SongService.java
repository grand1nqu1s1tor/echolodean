package dev.dipesh.service;

import dev.dipesh.entity.Song;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface SongService {
    List<String> extractSongIds(String jsonResponse);

    List<Song> fetchAndSaveSongDetails(List<String> songIds);

    Song saveSongDetails(String responseBody) throws IOException;

    Song getSongByAPI(String id);

    List<Song> findSongsByUserId(String userId);

    public Page<Song> getTrendingSongs(int limit);

    public List<Song> getLikedSongs();

    public boolean likeOrUnlikeSong(String songId, String userId);

    boolean isLiked(String songId, String userId);
}
