package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.entity.Song;
import dev.dipesh.gui.components.AudioPlayerComponent;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Route("trending")
@UIScope
public class TrendingMusicDashboard extends VerticalLayout implements UserDashboardView.UpdatableTabContent {

    private Grid<Song> grid = new Grid<>(Song.class);
    private final SongService songService;
    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();
    private final SongUtilityService songUtilityService;

    @Autowired
    public TrendingMusicDashboard(SongService songService, SongUtilityService songUtilityService) {
        this.songService = songService;
        this.songUtilityService = songUtilityService;
        setupGrid();
        add(grid);
        updateList();
        audioPlayerComponent.setVisible(false);
        add(audioPlayerComponent);
    }


    private void setupGrid() {
        grid.setColumns("title");
        grid.setAllRowsVisible(true);
        grid.addColumn(new ComponentRenderer<>(song -> {
            Button playButton = new Button("Play", click -> {
                songUtilityService.playSong(song, audioPlayerComponent);
            });
            return playButton;
        })).setHeader("Audio");

        grid.addColumn(new ComponentRenderer<>(song -> {
            String userId = songUtilityService.getCurrentUserId();
            Button likeButton = new Button(songService.isLiked(song.getSongId(), userId) ? "Unlike" : "Like");
            likeButton.addClickListener(click -> {
                songUtilityService.handleLikeClick_2(song, likeButton, grid);
            });
            // rest of your button setup
            return likeButton;
        })).setHeader("Like/Unlike");
    }

    private void updateList() {
        // Adjust the pagination if needed, here assumed it fetches first 10 songs
        Page<Song> trendingMusic = songService.getTrendingSongs(10);
        grid.setItems(trendingMusic.getContent());
    }

    @Override
    public void updateContent() {
        updateList(); // Refresh the list
    }
}
