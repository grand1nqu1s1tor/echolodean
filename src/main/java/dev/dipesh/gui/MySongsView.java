package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.gui.components.AudioPlayerComponent;
import dev.dipesh.service.SongService;

import java.util.List;

@Route("my-songs")
@PageTitle("My Songs")
@CssImport("./styles/styles.css")
public class MySongsView extends VerticalLayout implements UserDashboardView.UpdatableTabContent{

        private final SongService songService;
    //private final SecuredViewAccessChecker accessChecker;
    private Grid<Song> grid = new Grid<>(Song.class);

    private final UserController userController;

    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();


    public MySongsView(SongService songService, UserController userController) {
        this.songService = songService;
        this.userController = userController;
        setSizeFull();
        configureGrid();
        add(grid);
        updateList();
        audioPlayerComponent.setVisible(false); //
        add(audioPlayerComponent);
    }

    private void configureGrid() {
        grid.addClassName("song-grid");
        grid.setSizeFull();
        grid.removeAllColumns(); // Clear any existing columns

        // Image column
        grid.addColumn(new ComponentRenderer<>(song -> {
            if (song.getImageUrl() != null) {

                Image image = new Image(song.getImageUrl(), "Album cover");
                image.setWidth("100px"); // Set width to make images consistent
                image.setHeight("100px");
                return image;
            }

            return null;
        })).setHeader("Cover").setAutoWidth(true);

        // Title column
        grid.addColumn(Song::getTitle).setHeader("Title")
                .setAutoWidth(true)
                .setFlexGrow(0); // Prevent the column from stretching

        // Inside the configureGrid method after setting up the play button
        grid.addColumn(new ComponentRenderer<>(song -> {
            Button playButton = new Button("Play", click -> {
                audioPlayerComponent.setSource(song.getAudioUrl());
                audioPlayerComponent.setAlbumCover(song.getImageUrl());
                audioPlayerComponent.setTitle(song.getTitle());
                audioPlayerComponent.setLyrics(song.getLyrics());
                audioPlayerComponent.setVisible(true); // Show the audio player
            });
            playButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return playButton;
        })).setHeader("Audio");

        //Like Column
        grid.addColumn(new ComponentRenderer<>(song -> {
            String userId = getCurrentUserId(); // Retrieve the logged-in user's ID
            Button likeButton = new Button(songService.isLiked(song.getSongId(), userId) ? "Unlike" : "Like");

            likeButton.addClickListener(click -> {
                boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
                if (success) {
                    // Re-check the like state after the operation
                    boolean newLikeStatus = songService.isLiked(song.getSongId(), userId);
                    likeButton.setText(newLikeStatus ? "Unlike" : "Like"); // Update the text to the new state
                    grid.getDataProvider().refreshItem(song);
                }
            });

            likeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return likeButton;
        })).setHeader("Like/Unlike").setAutoWidth(true);

        grid.getColumns().forEach(col -> {
            col.setResizable(true);
            col.getElement().getStyle().set("text-align", "center");
        });

        grid.setAllRowsVisible(true);
    }


    // Method to play a song
    private void playSong(Song song) {
        audioPlayerComponent.setSource(song.getAudioUrl());
        audioPlayerComponent.setLyrics(song.getLyrics());
        audioPlayerComponent.setAlbumCover(song.getImageUrl());
        audioPlayerComponent.setTitle(song.getTitle());
        audioPlayerComponent.setVisible(true);
    }


    private void updateList() {
        String currentUserId = getCurrentUserId();
        List<Song> songs = songService.findSongsByUserId(currentUserId);
        grid.setItems(songs);
    }

    private String getCurrentUserId() {
        return userController.getCurrentUser().getUserId();
    }

    @Override
    public void updateContent() {
        updateList(); // Refresh the list
    }
}
