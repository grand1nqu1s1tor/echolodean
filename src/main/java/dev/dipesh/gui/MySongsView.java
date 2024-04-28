package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.dipesh.entity.Song;
import dev.dipesh.gui.components.AudioPlayerComponent;
import dev.dipesh.service.SongService;

import java.util.List;

@Route("my-songs")
@PageTitle("My Songs")
@CssImport("./styles/styles.css")
public class MySongsView extends VerticalLayout  {

    private final SongService songService;
    //private final SecuredViewAccessChecker accessChecker;
    private Grid<Song> grid = new Grid<>(Song.class);

    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent(); // Here is the custom audio player


    public MySongsView(SongService songService, SecuredViewAccessChecker accessChecker) {
        this.songService = songService;
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
            playButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return playButton;
        })).setHeader("Audio");

        //Like Column
        grid.addColumn(new ComponentRenderer<>(song -> {
            String userId = getCurrentUserId(); // Retrieve the logged-in user's ID
            boolean isLiked = songService.isLiked(song.getSongId(), userId);
            Button likeButton = new Button(isLiked ? "Unlike" : "Like");
            likeButton.addClickListener(click -> {
                boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
                if (success) {
                    likeButton.setText(isLiked ? "Like" : "Unlike"); // Toggle the text based on the current like status
                    songService.likeOrUnlikeSong(song.getSongId(), userId); // Optimistically update the like status
                    grid.getDataProvider().refreshItem(song); // Refresh the item in the grid
                }
            });

            likeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return likeButton;
        })).setHeader("Like/Unlike").setAutoWidth(true);


        // Adjust the size of the grid's columns
        grid.getColumns().forEach(col -> {
            col.setResizable(true);
            col.getElement().getStyle().set("text-align", "center");
        });

        // Adjust the height of the rows
        grid.setAllRowsVisible(true);
    }


    // Method to play a song
    private void playSong(Song song) {
        audioPlayerComponent.setSource(song.getAudioUrl());
        audioPlayerComponent.setLyrics(song.getLyrics());
        audioPlayerComponent.setAlbumCover(song.getImageUrl()); // Set the album cover
        audioPlayerComponent.setTitle(song.getTitle()); // Set the song title
        audioPlayerComponent.setVisible(true);
    }


    private void updateList() {
        String currentUserId = getCurrentUserId(); // Implement this method to obtain the current user's ID
        List<Song> songs = songService.findSongsByUserId(currentUserId);
        grid.setItems(songs);
    }

    private String getCurrentUserId() {
        // Obtain the current user's ID from the security context or however you store the current user
        // This is just a placeholder, and you will need to replace it with your actual user ID retrieval logic
        return "currentUser";
    }
}
