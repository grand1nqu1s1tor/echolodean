package dev.dipesh.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.gui.components.AudioPlayerComponent;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UIScope
@Route("liked-songs")
public class LikedSongsDashboard extends VerticalLayout implements UserDashboardView.UpdatableTabContent {

    private Grid<Song> grid = new Grid<>(Song.class);
    private final SongService songService;
    private final UserController userController;
    private final SongUtilityService songUtilityService;
    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();

    @Autowired
    public LikedSongsDashboard(SongService songService, UserController userController, SongUtilityService songUtilityService) {
        this.songService = songService;
        this.userController = userController;
        this.songUtilityService = songUtilityService;
        setupGrid();
        add(grid);
        grid.addColumn(new ComponentRenderer<>(song -> {
            Button likeButton = new Button("Unlike"); // Default text is "Unlike" since all these songs are liked
            likeButton.addClickListener(click -> {
                songUtilityService.handleLikeClick(song, likeButton, grid);
                likeButton.setText("Like"); // Change the text to "Like" since the user has unliked the song
                updateList(); // Refresh the list to reflect the change
            });
            likeButton.addThemeVariants(ButtonVariant.LUMO_ERROR); // Use a different style for the unlike button
            return likeButton;
        })).setHeader("Like/Unlike").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(song -> {
            Button playButton = new Button("Play", click -> playSong(song));
            playButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            return playButton;
        })).setHeader("Audio");

        audioPlayerComponent.setVisible(false); //
        add(audioPlayerComponent);
        updateList();
    }

    private void playSong(Song song) {
        audioPlayerComponent.setSource(song.getAudioUrl());
        audioPlayerComponent.setLyrics(song.getLyrics());
        audioPlayerComponent.setAlbumCover(song.getImageUrl());
        audioPlayerComponent.setTitle(song.getTitle());
        audioPlayerComponent.setVisible(true);
    }

    private void setupGrid() {
        grid.setColumns("title");
        grid.setAllRowsVisible(true);
    }

    private void updateList() {
        UI.getCurrent().access(() -> {
            String userId = userController.getCurrentUser().getUserId();
            if (userId != null) {
                List<Song> likedMusic = songService.getLikedSongsByUserId(userId);
                grid.setItems(likedMusic);
            }
        });
    }

    @Override
    public void updateContent() {
        updateList();
        audioPlayerComponent.setVisible(false);// Refresh the list
    }
}

