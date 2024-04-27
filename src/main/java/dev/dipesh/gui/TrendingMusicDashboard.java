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
@Route("trending")
public class TrendingMusicDashboard extends VerticalLayout {

    private Grid<Song> grid = new Grid<>(Song.class);

    private final SongService songService;

    @Autowired
    public TrendingMusicDashboard(SongService songService) {
        this.songService = songService;
        setupGrid();
        add(grid);
        updateList();  // Populate grid with data
    }


    private void setupGrid() {
        grid.setColumns("title");
        grid.setAllRowsVisible(true);
    }

    private void updateList() {
        List<Song> trendingMusic = songService.getTrendingSongs(10);
        grid.setItems(trendingMusic);
    }
}
