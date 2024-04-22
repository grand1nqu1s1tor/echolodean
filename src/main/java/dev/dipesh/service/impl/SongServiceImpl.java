package dev.dipesh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dipesh.entity.Song;
import dev.dipesh.repository.SongsRepository;
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
    private HttpClient client;
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


    @Override
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

}
