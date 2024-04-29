package dev.dipesh.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.controller.UserController;
import dev.dipesh.entity.Song;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UIScope
@Route("liked-songs")
public class LikedSongsDashboard extends VerticalLayout {

    private Grid<Song> grid = new Grid<>(Song.class);
    private final SongService songService;
    private final UserController userController;


    @Autowired
    public LikedSongsDashboard(SongService songService, UserController userController) {
        this.songService = songService;
        this.userController = userController;
        setupGrid();
        add(grid);
        updateList();
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
}

