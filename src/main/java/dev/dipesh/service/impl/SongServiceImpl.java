package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.dto.SongResponseDTO;
import dev.dipesh.entity.Song;
import dev.dipesh.entity.SongLike;
import dev.dipesh.entity.User;
import dev.dipesh.repository.SongLikeRepository;
import dev.dipesh.repository.SongRepository;
import dev.dipesh.repository.UserRepository;
import dev.dipesh.service.SongService;
import dev.dipesh.util.ApiUrlConstants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
    private final HttpClient httpClient;
    private final SongRepository songRepository;
    private final SongLikeRepository songLikeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    @Autowired
    public SongServiceImpl(HttpClient httpClient, SongRepository songRepository,
                           SongLikeRepository songLikeRepository, UserRepository userRepository,
                           @Value("${api.key}") String apiKey) {
        this.httpClient = httpClient;
        this.songRepository = songRepository;
        this.songLikeRepository = songLikeRepository;
        this.userRepository = userRepository;
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ArrayList<String> extractSongIds(String jsonResponse) {
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
            LOGGER.log(Level.SEVERE, "Error during parsing of the response", e);
        }
        return songIds;
    }


    @Override
    public List<Song> fetchAndSaveSongDetails(List<String> songIds) {
        List<Song> songDetails = new ArrayList<>();
        for (String songId : songIds) {
            try {
                String url = ApiUrlConstants.GET_SONG + songId;
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
        JsonNode rootNode = objectMapper.readTree(responseBody);

        if (!rootNode.path("data").isObject()) {
            throw new IllegalArgumentException("Expected JSON object in the 'data' field of the response");
        }
        JsonNode dataNode = rootNode.path("data");
        Song song = objectMapper.treeToValue(dataNode, Song.class);

        if (song != null) {
            songRepository.save(song);
        }
        return song;
    }


    public Song getSongByAPI(String songId) {
        String url = ApiUrlConstants.GATEWAY_FEED + songId;
        HttpRequest request = buildHttpRequest(url);
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseSong(response.body());
            } else {
                LOGGER.log(Level.WARNING, "Received non-OK status from server: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during HTTP request", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    private Song parseSong(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.path("code").asInt() == 0 && "success".equals(root.path("msg").asText())) {
                JsonNode dataNode = root.path("data");
                return objectMapper.treeToValue(dataNode, Song.class);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during parsing of the response", e);
        }
        return null;
    }


    @Override
    public List<Song> findSongsByUserId(String userId) {
        return songRepository.findByUserId(userId);
    }

    @Override
    public Page<Song> getTrendingSongs(int limit) {
        return songRepository.findTopTrendingSongs(PageRequest.of(0, limit));
    }

    @Override
    public List<Song> getLikedSongsByUserId(String userId) {
        return songRepository.findLikedSongsByUserId(userId);
    }

    @Transactional
    public boolean likeOrUnlikeSong(String songId, String userId) {
        Song song = songRepository.findById(songId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (song == null || user == null) {
            return false;
        }
        Optional<SongLike> existingLike = songLikeRepository.findBySongAndUser(song, user);
        if (existingLike.isPresent()) {
            songLikeRepository.delete(existingLike.get());
            return false;
        } else {
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
        return songRepository.findSongsWithMissingMetadata();
    }

    @Transactional
    public void updateSongDetails(String songId, SongResponseDTO details) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null && details != null) {
            song.setAudioUrl(details.getData().getAudioUrl());
            song.setImageUrl(details.getData().getImageUrl());
            song.setTitle(details.getData().getTitle());
            song.setLyrics(details.getData().getMetaPrompt());
            songRepository.save(song);
        }
    }

    private HttpRequest buildHttpRequest(String uri) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .header("api-key", apiKey)
                .build();
    }


}


