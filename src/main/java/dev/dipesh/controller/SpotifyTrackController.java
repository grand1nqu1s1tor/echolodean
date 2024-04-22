package dev.dipesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.DTO.ApiResponseDTO;
import dev.dipesh.DTO.PromptRequestDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
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
        ApiResponseDTO sunoApiResponse = externalAPIService.postGenerateDescription("http://localhost:8000/generate/description-mode", requestBody);

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


    @GetMapping("/suno")
    public Song getSongsByIds(@PathVariable String songId) {
        return songService.getSongById(songId);
    }
}