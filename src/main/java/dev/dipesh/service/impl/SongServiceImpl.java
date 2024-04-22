package dev.dipesh.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.entity.Song;
import dev.dipesh.repository.SongsRepository;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private HttpClient httpClient;

    private final String baseUrl = "https://api.sunoapiapi.com/api/v1/gateway/feed/";
    private final String apiKey = "4mANlXLpmBdJOqVkeYxjgWDvkF0G6gz+";

    @Override
    public ArrayList<String> extractSongIds(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> songsIds = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode clipsNode = rootNode.path("clips");
            if (clipsNode.isArray()) {
                for (JsonNode clipNode : clipsNode) {
                    String clipId = clipNode.path("id").asText();
                    System.out.println("Clip ID: " + clipId);
                    songsIds.add(clipId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songsIds;
    }


    /*    @Override
        public List<Song> fetchAndSaveSongDetails(List<String> songIds) {
            List<Song> songDetails = new ArrayList<>();
            for (String songId : songIds) {
                try {
                    String url = "http://localhost:8000/feed/" + songId;
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(url))
                            .header("Accept", "application/json")
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        Song song = saveSongDetails(response.body());
                        if (song != null) {
                            songDetails.add(song);
                        }
                    } else {
                        // Log or handle the error appropriately
                    }
                } catch (IOException | InterruptedException e) {
                    // Log the exception
                }
            }
            return songDetails;
        }*/
    @Override
    public List<Song> fetchAndSaveSongDetails(List<String> songIds) {
        List<Song> songDetails = new ArrayList<>();
        String baseUrl = "https://api.sunoapiapi.com/api/v1/";
        for (String songId : songIds) {
            try {
                String url = baseUrl + "gateway/feed/" + songId; // Update the URL
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .header("api-key", apiKey) // Use the header parameter as per API documentation
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Song song = saveSongDetails(response.body());
                    if (song != null) {
                        songDetails.add(song);
                    }
                } else {
                    System.out.println("SOme Error Occured");
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        return songDetails;
    }


    @Override
    public Song saveSongDetails(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        Song song = null;

        if (rootNode.isArray()) {
            for (JsonNode item : rootNode) {
                song = new Song();
                song.setSongId(item.path("id").asText());
                song.setUserId("exampleUserId"); // This should ideally come from a secure, authenticated source
                song.setTitle(item.path("title").asText());
                song.setDescription(item.path("metadata").path("prompt").asText());
                song.setDuration(item.path("duration").asDouble());
                song.setAudioUrl(item.path("audio_url").asText());
                song.setImageUrl(item.path("image_url").asText());
                song.setLyrics(item.path("lyrics").asText()); // Assuming 'lyrics' is available in the response
                song.setLikes(0); // Initialize likes to zero

                songsRepository.save(song);
            }
        } else {
            System.out.println("Unable to add song to DB, JSON response is not an array.");
        }
        return song; // Return the last song processed or modify logic to return a list if multiple
    }

    @Override
    public Song getSongById(String id) {
        String url = baseUrl + id;
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
                // Handle non-200 status codes appropriately
            }
        } catch (IOException | InterruptedException e) {
            // Handle the exception appropriately
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

}
