package dev.dipesh.service;

import dev.dipesh.entity.Song;

import java.io.IOException;
import java.util.List;

public interface SongService {
    List<String> extractSongIds(String jsonResponse);
    List<Song> fetchAndSaveSongDetails(List<String> songIds);
    Song saveSongDetails(String responseBody) throws IOException;
    Song getSongById(String id);
}
