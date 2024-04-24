package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.entity.Song;
import dev.dipesh.repository.SongsRepository;
import dev.dipesh.service.SongService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private HttpClient httpClient;

    private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
    private final String baseUrl = "https://api.sunoaiapi.com/api/v1/";
    private final String apiKey = "4mANlXLpmBdJOqVkeYxjgWDvkF0G6gz+";

    @Override
    public ArrayList<String> extractSongIds(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> songIds = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode dataArray = rootNode.path("data");
            if (dataArray.isArray()) {
                for (JsonNode dataNode : dataArray) {
                    String songId = dataNode.path("song_id").asText();
                    System.out.println("Song ID: " + songId);
                    songIds.add(songId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songIds;
    }


    @Override
    public List<Song> fetchAndSaveSongDetails(List<String> songIds) {
        List<Song> songDetails = new ArrayList<>();
        for (String songId : songIds) {
            try {
                String url = baseUrl + "gateway/feed/" + songId;
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .header("api-key", apiKey)
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Song song = saveSongDetails(response.body());
                    if (song != null) {
                        songDetails.add(song);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Failed to fetch song details for ID: " + songId + ", Status code: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error fetching song details for song ID: " + songId, e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return songDetails;
    }


    @Override
    @Transactional
    public Song saveSongDetails(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // Check if the 'data' field is an object and not an array
        if (!rootNode.path("data").isObject()) {
            throw new IllegalArgumentException("Expected JSON object in the 'data' field of the response");
        }

        // Extract the 'data' field, which is the song details
        JsonNode dataNode = rootNode.path("data");
        Song song = objectMapper.treeToValue(dataNode, Song.class);

        if (song != null) {
            // Here you would fetch the actual userId of the logged-in user, not just "currentUser"
            song.setUserId("currentUser");
            song.setLikes(0); // Initialize likes to zero
            songsRepository.save(song);
        }

        return song; // Now it only returns a single Song object
    }



    //If the user/frontend knows the song id and want to query the Suno API for the song details.
    public Song getSongByAPI(String id) {
        String url = baseUrl + "gateway/feed/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseSong(response.body());
            } else {
                LOGGER.log(Level.WARNING, "Received non-OK status from server: " + response.statusCode());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during HTTP request", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Request interrupted", e);
        } catch (Exception e) { // Catching parsing or other unexpected exceptions
            LOGGER.log(Level.SEVERE, "Error parsing the song data", e);
        }
        return null;
    }

    private Song parseSong(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.path("code").asInt() == 0 && "success".equals(root.path("msg").asText())) {
                JsonNode dataNode = root.path("data");
                return objectMapper.treeToValue(dataNode, Song.class);
            }
        } catch (IOException e) {
            // Handle the exception appropriately
        }
        return null;
    }


    @Override
    public List<Song> findSongsByUserId(String userId) {
        return songsRepository.findByUserId(userId);
    }
}
