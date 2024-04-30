package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.entity.Song;
import dev.dipesh.vaadin.components.AudioPlayerComponent;
import dev.dipesh.vaadin.util.SongUtilityService;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Route("trending")
@UIScope
public class TrendingDashboardView extends VerticalLayout implements UserDashboardView.UpdatableTabContent {

    private Grid<Song> grid = new Grid<>(Song.class);
    private final SongService songService;
    private AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();
    private final SongUtilityService songUtilityService;

    @Autowired
    public TrendingDashboardView(SongService songService, SongUtilityService songUtilityService) {
        this.songService = songService;
        this.songUtilityService = songUtilityService;
        setupGrid();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(grid);
        mainLayout.setFlexGrow(1, grid);

        audioPlayerComponent.setWidth("100%");
        mainLayout.add(audioPlayerComponent);

        add(mainLayout);
        updateList();
    }


    private void setupGrid() {
        grid.setColumns("title");
        grid.setAllRowsVisible(true);

        grid.addComponentColumn(song -> {
            Button playButton = new Button("Play", click -> {
                songUtilityService.playSong(song, audioPlayerComponent);
            });
            return playButton;
        }).setHeader("Audio");

        grid.addComponentColumn(song -> {
            String userId = songUtilityService.getCurrentUserId();
            Button likeButton = new Button(songService.isLiked(song.getSongId(), userId) ? "Unlike" : "Like");
            likeButton.addClickListener(click -> {
                songUtilityService.handleLikeClick_2(song, likeButton, grid);
            });
            return likeButton;
        }).setHeader("Like/Unlike");

        grid.addComponentColumn(song -> {
            AudioPlayerComponent audioPlayerComponent = new AudioPlayerComponent();
            audioPlayerComponent.setVisible(false);
            return audioPlayerComponent;
        }).setSortable(false).setFlexGrow(0);
    }

    private void updateList() {
        Page<Song> trendingMusic = songService.getTrendingSongs(10);
        grid.setItems(trendingMusic.getContent());
    }

    @Override
    public void updateContent() {
        updateList();
    }
}
