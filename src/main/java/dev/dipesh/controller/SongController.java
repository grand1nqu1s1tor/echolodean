package dev.dipesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.dto.ApiResponseDTO;
import dev.dipesh.dto.PromptRequestDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.service.SongService;
import dev.dipesh.util.ApiUrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/songs")
@RestController
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private ExternalAPIService externalAPIService;

    @Autowired
    private UserController userController;

    @PostMapping("/generate")
    public ResponseEntity<List<Song>> generateSong(@RequestBody PromptRequestDTO promptRequestDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(promptRequestDTO);
            ApiResponseDTO apiResponse = externalAPIService.postGenerateDescription(ApiUrlConstants.GENERATE_SONG, requestBody);
            if (!apiResponse.isSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            List<String> songIds = songService.extractSongIds(apiResponse.getBody());
            if (songIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Song> songs = songService.fetchAndSaveSongDetails(songIds);
            return ResponseEntity.ok(songs);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/suno/{songId}")
    public ResponseEntity<Song> getSongById(@PathVariable String songId) {
        Song song = songService.getSongByAPI(songId);
        if (song != null) {
            return ResponseEntity.ok(song);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Song>> getSongsByUserId(@PathVariable String userId) {
        List<Song> songs = songService.findSongsByUserId(userId);
        if (!songs.isEmpty()) {
            return ResponseEntity.ok(songs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/fetch-and-save")
    public ResponseEntity<List<Song>> fetchAndSaveSongDetails(@RequestBody List<String> songIds) {
        try {
            List<Song> songs = songService.fetchAndSaveSongDetails(songIds);
            return ResponseEntity.ok(songs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<Song>> getTrendingSongs() {
        Page<Song> trendingSongs = songService.getTrendingSongs(10);
        return ResponseEntity.ok(trendingSongs);
    }

    @PostMapping("/like-unlike/{songId}")
    public ResponseEntity<String> likeOrUnlikeSong(@PathVariable String songId) {
        String userId = userController.getCurrentUser().getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be logged in to like a song.");
        }
        boolean liked = songService.likeOrUnlikeSong(songId, userId);
        return liked ? ResponseEntity.ok("Song liked successfully.") : ResponseEntity.ok("Song unliked successfully.");
    }
}
