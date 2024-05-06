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
    private final ExternalAPIService externalAPIService;

    public SongUpdateScheduler(SongService songService, ExternalAPIService externalAPIService) {
        this.songService = songService;
        this.externalAPIService = externalAPIService;
    }

    @Scheduled(fixedDelay = 10000)
    public void updateSongDetails() {
        List<Song> songsToUpdate = songService.findSongsWithMissingMetadata();
        for (Song song : songsToUpdate) {

            CompletableFuture<Boolean> completionFuture = externalAPIService.checkForCompletion(song.getSongId());

            completionFuture.thenCompose(complete -> {
                if (complete) {
                    return externalAPIService.getSongDetails(song.getSongId())
                            .thenAccept(songDetails -> {
                                if (songDetails != null && songDetails.getCode() == 0 && songDetails.getData() != null) {
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
