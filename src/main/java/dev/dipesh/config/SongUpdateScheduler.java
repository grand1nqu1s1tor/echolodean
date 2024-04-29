package dev.dipesh.config;

import dev.dipesh.entity.Song;
import dev.dipesh.service.ExternalAPIService;
import dev.dipesh.service.SongService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Profile("scheduling-enabled")
public class SongUpdateScheduler {

    private final SongService songService;
    private final ExternalAPIService externalAPIService; // Assuming this is the service that interacts with the external API

    // Use constructor injection to inject the services
    public SongUpdateScheduler(SongService songService, ExternalAPIService externalAPIService) {
        this.songService = songService;
        this.externalAPIService = externalAPIService;
    }

    // The method will be executed every 10 seconds (10000 milliseconds)
    @Scheduled(fixedDelay = 30000)
    public void updateSongDetails() {
        List<Song> songsToUpdate = songService.findSongsWithMissingMetadata();
        for (Song song : songsToUpdate) {

            CompletableFuture<Boolean> completionFuture = externalAPIService.checkForCompletion(song.getSongId());

            // Chain CompletableFuture to handle it properly
            completionFuture.thenCompose(complete -> {
                if (complete) {
                    // Only fetch details if the song is complete
                    return externalAPIService.getSongDetails(song.getSongId())
                            .thenAccept(songDetails -> {
                                if (songDetails != null && songDetails.getCode() == 0 && songDetails.getData() != null) {
                                    // Assuming updateSongDetails expects SongData object
                                    songService.updateSongDetails(song.getSongId(), songDetails);
                                }
                            });
                } else {
                    return CompletableFuture.completedFuture(null);
                }
            }).exceptionally(e -> {
                System.err.println("Error in processing song ID " + song.getSongId() + ": " + e.getMessage());
                return null;
            });
        }
    }

}
