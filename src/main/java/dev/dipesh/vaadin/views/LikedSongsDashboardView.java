package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.vaadin.components.AudioPlayerComponent;
import dev.dipesh.vaadin.util.SongUtilityService;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UIScope
@Route("liked-songs")
public class LikedSongsDashboardView extends VerticalLayout implements UserDashboardView.UpdatableTabContent {

    private Grid<Song> grid = new Grid<>(Song.class);
    private final SongService songService;
    private final UserController userController;
    private final SongUtilityService songUtilityService;
    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();

    @Autowired
    public LikedSongsDashboardView(SongService songService, UserController userController, SongUtilityService songUtilityService) {
        this.songService = songService;
        this.userController = userController;
        this.songUtilityService = songUtilityService;
        setupGrid();

        // Create a new HorizontalLayout to position elements side-by-side
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull(); // Ensure the layout occupies the full available space

        // Add the grid to the left side of the layout
        mainLayout.add(grid);
        mainLayout.setFlexGrow(1, grid); // Allow grid to expand and fill remaining space horizontally

        // Adjust the audio player component width
        audioPlayerComponent.setWidth("100%"); // Set audio player component width to 100% to fill the remaining space

        // Add the audio player component to the right side of the layout
        mainLayout.add(audioPlayerComponent);

        add(mainLayout); // Add the main layout containing both grid and player
        updateList();

        grid.addColumn(new ComponentRenderer<>(song -> {
            Button likeButton = new Button("Unlike");
            likeButton.addClickListener(click -> {
                songUtilityService.handleLikeClick(song, likeButton, grid);
                likeButton.setText("Like"); // Change the text to "Like" since the user has unliked the song
                updateList();
            });
            likeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
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
        audioPlayerComponent.setVisible(false);
    }
}

