/*
package dev.dipesh.gui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Route("home")
@PageTitle("Test to be changed")
public class TestDashBoardView extends VerticalLayout implements RouterLayout {
    private Tabs tabs = new Tabs();
    private Map<Tab, Component> tabsToPages = new HashMap<>();
    private SongService songService;

    @Autowired
    public TestDashBoardView(SongService songService) {
        this.songService = songService;
        setSizeFull();

        Tab trendingMusicTab = new Tab("Trending Music");
        Tab createSong = new Tab("Create your own Song");
        Tab mySongsTab = new Tab("Your Creations");
        Tab likedSongsTab = new Tab("All you like");
        Tab profileTab = new Tab("Profile");


        Component createSongDashBoard = new TrendingMusicDashboard(this.songService);
        Component trendingMusicDashboard = new TrendingMusicDashboard(this.songService);
        Component mySongsDashBoard = new TrendingMusicDashboard(this.songService);
        Component likedSongsDashBoard = new TrendingMusicDashboard(this.songService);
        Component profileDashBoard = new TrendingMusicDashboard(this.songService);

        tabsToPages.put(trendingMusicTab, trendingMusicDashboard);
        tabsToPages.put(createSong, createSongDashBoard);
        tabsToPages.put(mySongsTab, mySongsDashBoard);
        tabsToPages.put(likedSongsTab, likedSongsDashBoard);
        tabsToPages.put(profileTab, profileDashBoard);


        tabs.add(trendingMusicTab);
        tabs.setFlexGrowForEnclosedTabs(1);

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            Component selectedContent = tabsToPages.get(selectedTab);
            removeAll(); // Clear existing components
            add(tabs, selectedContent); // Add tabs and the selected component
        });

        add(tabs, createSongDashBoard,trendingMusicDashboard, mySongsDashBoard, likedSongsDashBoard, profileDashBoard);
    }
}
*/
