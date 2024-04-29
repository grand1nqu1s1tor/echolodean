package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.DTO.SongResponseDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.entity.SongLike;
import dev.dipesh.entity.User;
import dev.dipesh.repository.SongLikeRepository;
import dev.dipesh.repository.SongRepository;
import dev.dipesh.repository.UserRepository;
import dev.dipesh.service.SongService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private SongLikeRepository songLikeRepository;
    @Autowired
    private UserRepository userRepository;

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
            song.setUserId("31jz2umx7gs26nk3lh3w6lfy33ry");
            songRepository.save(song);
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


    //CHange to findGeneratedSongsByUserId
    @Override
    public List<Song> findSongsByUserId(String userId) {
        return songRepository.findByUserId(userId);
    }

    @Override
    public Page<Song> getTrendingSongs(int limit) {
        return songRepository.findTopTrendingSongs(PageRequest.of(0, limit));
    }
    @Override
    public List<Song> getLikedSongsByUserId(String userId){
        return songRepository.findLikedSongsByUserId(userId);
    }

    @Transactional
    public boolean likeOrUnlikeSong(String songId, String userId) {
        // Find the Song and User entities
        Song song = songRepository.findById(songId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (song == null || user == null) {
            // Song or user does not exist, cannot proceed
            return false;
        }
        // Check if the like already exists
        Optional<SongLike> existingLike = songLikeRepository.findBySongAndUser(song, user);
        if (existingLike.isPresent()) {
            // User already liked this song, so unlike it
            songLikeRepository.delete(existingLike.get());
            return false; // You can also choose to return 'true' to indicate a successful unlike operation
        } else {
            // Like the song since it's not already liked
            SongLike newLike = new SongLike();
            newLike.setSong(song);
            newLike.setUser(user);
            songLikeRepository.save(newLike);
            return true;
        }
    }

    @Override
    public boolean isLiked(String songId, String userId) {
        Song song = songRepository.findById(songId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (song == null || user == null) {
            return false;
        }
        return songLikeRepository.existsBySongAndUser(song, user);
    }


    public List<Song> findSongsWithMissingMetadata() {
        // This would be a query in your repository to find songs where audio_url or image_url is null
        return songRepository.findSongsWithMissingMetadata();
    }

    @Transactional
    public void updateSongDetails(String songId, SongResponseDTO details) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null && details != null) {
            // Assuming details contains fields like audioUrl, imageUrl, etc.
            song.setAudioUrl(details.getData().getAudioUrl());
            song.setImageUrl(details.getData().getImageUrl());
            song.setTitle(details.getData().getTitle());
            song.setLyrics(details.getData().getMetaPrompt());
            songRepository.save(song);
        }
    }

}


