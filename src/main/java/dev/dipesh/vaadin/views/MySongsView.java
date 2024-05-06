package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.service.SongService;
import dev.dipesh.vaadin.components.AudioPlayerComponent;

import java.util.List;

@Route("my-songs")
@PageTitle("My Songs")
@CssImport("./styles/styles.css")
public class MySongsView extends VerticalLayout implements UserDashboardView.UpdatableTabContent {

    private final SongService songService;
    private Grid<Song> grid = new Grid<>(Song.class);

    private final UserController userController;

    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();


    public MySongsView(SongService songService, UserController userController) {
        this.songService = songService;
        this.userController = userController;
        setSizeFull();
        configureGrid();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

        mainLayout.add(grid);
        mainLayout.setFlexGrow(1, grid);

        audioPlayerComponent.setWidth("100%");

        mainLayout.add(audioPlayerComponent);

        add(mainLayout);
        updateList();

        audioPlayerComponent.setVisible(false);
    }

    private void configureGrid() {
        grid.addClassName("song-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(new ComponentRenderer<>(song -> {
            if (song.getImageUrl() != null) {
                Image image = new Image(song.getImageUrl(), "Album cover");
                image.setWidth("100px");
                image.setHeight("100px");
                return image;
            }
            return null;
        })).setHeader("Cover").setAutoWidth(true);

        grid.addColumn(Song::getTitle).setHeader("Title")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(song -> {
            Button playButton = new Button("Play", click -> {
                audioPlayerComponent.setSource(song.getAudioUrl());
                audioPlayerComponent.setAlbumCover(song.getImageUrl());
                audioPlayerComponent.setTitle(song.getTitle());
                audioPlayerComponent.setLyrics(song.getLyrics());
                audioPlayerComponent.setVisible(true);
            });
            playButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return playButton;
        })).setHeader("Audio");

        grid.addColumn(new ComponentRenderer<>(song -> {
            String userId = getCurrentUserId();
            Button likeButton = new Button(songService.isLiked(song.getSongId(), userId) ? "Unlike" : "Like");

            likeButton.addClickListener(click -> {
                boolean success = songService.likeOrUnlikeSong(song.getSongId(), userId);
                if (success) {
                    boolean newLikeStatus = songService.isLiked(song.getSongId(), userId);
                    likeButton.setText(newLikeStatus ? "Unlike" : "Like");
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

        grid.getElement().getStyle().set("background-color", "transparent");

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
