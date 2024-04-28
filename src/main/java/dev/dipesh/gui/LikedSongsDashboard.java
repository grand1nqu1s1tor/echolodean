package dev.dipesh.gui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import dev.dipesh.entity.Song;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Route("liked-songs")
public class LikedSongsDashboard extends VerticalLayout {

    private Grid<Song> grid = new Grid<>(Song.class);

    private final SongService songService;

    @Autowired
    public LikedSongsDashboard(SongService songService) {
        this.songService = songService;
        setupGrid();
        add(grid);
        updateList();
    }


    private void setupGrid() {
        grid.setColumns("title");
        grid.setAllRowsVisible(true);
    }

    private void updateList() {
        List<Song> likedMusic = songService.getLikedSongs();
        grid.setItems(likedMusic);
    }
}

