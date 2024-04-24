package dev.dipesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.DTO.ApiResponseDTO;
import dev.dipesh.DTO.PromptRequestDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/songs")
@RestController
public class SpotifyTrackController {
    @Autowired
    private SongService songService;
    @Autowired
    private ExternalAPIService externalAPIService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateSong(@RequestBody PromptRequestDTO promptRequestDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(promptRequestDTO);
        ApiResponseDTO sunoApiResponse = externalAPIService.postGenerateDescription("https://api.sunoaiapi.com/api/v1/gateway/generate/gpt_desc", requestBody);

        if (!sunoApiResponse.isSuccessful()) {
            return ResponseEntity.badRequest().body(sunoApiResponse.getErrorMessage());
        }

        List<String> songIds = songService.extractSongIds(sunoApiResponse.getBody());
        if (songIds.isEmpty()) {
            return ResponseEntity.ok("No songs were generated.");
        }
        List<Song> songDetails = songService.fetchAndSaveSongDetails(songIds);
        return ResponseEntity.ok(songDetails);
    }


    @GetMapping("/suno/{songId}")
    public Song getSongsByIds(@PathVariable String songId) {
        return songService.getSongByAPI(songId);
    }

    @GetMapping("/user/{userId}")
    public List<Song> getSongsByUserId(@PathVariable String userId) {
        return songService.findSongsByUserId(userId);
    }


    //For developer use
    @PostMapping("/fetch-and-save")
    public ResponseEntity<List<Song>> fetchAndSaveSongDetails(@RequestBody List<String> songIds) {
        try {
            List<Song> songs = songService.fetchAndSaveSongDetails(songIds);
            return ResponseEntity.ok(songs);
        } catch (Exception e) {
            // Log the exception and handle the error response accordingly
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}