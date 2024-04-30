package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.gui.components.AudioPlayerComponent;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SongUtilityService {

    private final SongService songService;
    private final UserController userController;

    @Autowired
    public SongUtilityService(SongService songService, UserController userController) {
        this.songService = songService;
        this.userController = userController;
    }

    public void playSong(Song song, AudioPlayerComponent audioPlayerComponent) {
        audioPlayerComponent.setSource(song.getAudioUrl());
        audioPlayerComponent.setLyrics(song.getLyrics());
        audioPlayerComponent.setAlbumCover(song.getImageUrl());
        audioPlayerComponent.setTitle(song.getTitle());
        audioPlayerComponent.setVisible(true);
    }

    public void handleLikeClick(Song song, Button likeButton, Grid<Song> grid) {
        String userId = getCurrentUserId();
        boolean isLiked = songService.isLiked(song.getSongId(), userId);

        if (isLiked) {
            // The song is currently liked, so unliking should occur.
            boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
            if (success) {
                // Assuming the song has been unliked successfully.
                // You may want to remove the song from the grid immediately or refresh the list.
                grid.getDataProvider().refreshAll(); // Refreshing the entire list to remove the unliked song
            }
        } else {
            // If the song was not liked (which shouldn't happen in this view), handle accordingly.
            boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
            if (success) {
                likeButton.setText("Unlike");
                grid.getDataProvider().refreshItem(song);
            }
        }
    }

    public  String getCurrentUserId() {
        // Assuming userController.getCurrentUser() returns the currently logged-in user
        return userController.getCurrentUser().getUserId();
    }

    public void handleLikeClick_2(Song song, Button likeButton, Grid<Song> grid) {
        String userId = getCurrentUserId();
        boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
        if (success) {
            // Toggle the like status after a successful operation
            boolean newLikeStatus = songService.isLiked(song.getSongId(), userId);
            likeButton.setText(newLikeStatus ? "Unlike" : "Like");
            grid.getDataProvider().refreshItem(song);
            System.out.println("Like status updated for song: " + song.getTitle());
        } else {
            boolean newLikeStatus = songService.isLiked(song.getSongId(), userId);
            likeButton.setText(newLikeStatus ? "Like" : "Unlike");
            grid.getDataProvider().refreshItem(song);
        }
    }

}
