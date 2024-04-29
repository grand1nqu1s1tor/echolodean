package dev.dipesh.gui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.entity.Song;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Route("trending")
@UIScope
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
        // Adjust the pagination if needed, here assumed it fetches first 10 songs
        Page<Song> trendingMusic = songService.getTrendingSongs(10);
        grid.setItems(trendingMusic.getContent());
    }
}
