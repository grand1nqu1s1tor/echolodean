package dev.dipesh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.config.SpotifyConfiguration;
import dev.dipesh.dto.ApiResponseDTO;
import dev.dipesh.dto.PromptRequestDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.service.SongService;
import dev.dipesh.util.ApiUrlConstants;
import dev.dipesh.util.SpotifyGenreUtils;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import java.io.IOException;
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

    @Autowired
    private SpotifyController spotifyController;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    //TODO remove after testing
    @Autowired
    private HttpSession session;


    @PostMapping("/generate")
    public ResponseEntity<List<Song>> generateSong(@RequestBody PromptRequestDTO promptRequestDTO) {
        try {
            String customPrompt = addUserContext(promptRequestDTO.getGpt_description_prompt());
            promptRequestDTO.setGpt_description_prompt(customPrompt);
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(promptRequestDTO);
//            ApiResponseDTO apiResponse = ApiResponseDTO.parseResponse("{\n" + " \"code\": 0,\n" + " \"msg\": \"success\",\n" + " \"data\": [{\n" + "  \"user_id\": \"xxx\",\n" + "  \"song_id\": \"71540f19-d335-4938-95e7-52b3d524d17c\",\n" + "  \"status\": \"submitted\",\n" + "  \"title\": \"Happy dog Song\",\n" + "  \"image_large_url\": null,\n" + "  \"image_url\": null,\n" + "  \"model_name\": \"chirp-v3\",\n" + "  \"video_url\": \"\",\n" + "  \"audio_url\": \"\",\n" + "  \"meta_tags\": \"happy, rock\",\n" + "  \"meta_prompt\": \"A happy song about dogs\",\n" + "  \"meta_duration\": null,\n" + "  \"meta_error_msg\": null,\n" + "  \"meta_error_type\": null\n" + " }, {\n" + "  \"user_id\": \"xxx\",\n" + "  \"song_id\": \"afdcb554-249d-4d67-90e3-917f7b4f8bfe\",\n" + "  \"status\": \"submitted\",\n" + "  \"title\": \"Happy dog Song\",\n" + "  \"image_large_url\": null,\n" + "  \"image_url\": null,\n" + "  \"model_name\": \"chirp-v3\",\n" + "  \"video_url\": \"\",\n" + "  \"audio_url\": \"\",\n" + "  \"meta_tags\": \"happy, rock\",\n" + "  \"meta_prompt\": \"A happy song about dogs\",\n" + "  \"meta_duration\": null,\n" + "  \"meta_error_msg\": null,\n" + "  \"meta_error_type\": null\n" + " }]\n" + "}");
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

    private String addUserContext(String gptDescriptionPrompt) {
        try {
            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObjectFromSession(session);
            GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists().build();
            Paging<Artist> topArtists = getUsersTopArtistsRequest.execute();
            SpotifyGenreUtils spotifyGenreUtils = new SpotifyGenreUtils();
            List<String> userFavoriteGenres = spotifyGenreUtils.extractGenres(topArtists);
            System.out.println("Extracted Genres: " + userFavoriteGenres);
            return String.format(gptDescriptionPrompt + ". Genre: ", userFavoriteGenres.getFirst() + "\t" + userFavoriteGenres.getLast());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "";
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
