package dev.dipesh.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.dipesh.controller.UserController;
import dev.dipesh.gui.components.SongPromptCard;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@Route("home")
@PageTitle("Home")
public class UserDashboardView extends VerticalLayout {

    private Tabs tabs = new Tabs();
    private Map<Tab, Component> tabsToPages = new HashMap<>();
    private final SongService songService;
    private final UserController userController;
    private final SongUtilityService songUtilityService;


    @Autowired
    public UserDashboardView(SongPromptCard songPromptCard, SongService songService, UserController userController, SongUtilityService songUtilityService) {
        this.userController = userController;
        this.songService = songService;
        this.songUtilityService = songUtilityService;

        // User greeting
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        H1 greeting = new H1("Hello, " + userName + "!");
        add(greeting); // Add the greeting to the layout

        setSizeFull();

        Tab createSongTab = new Tab("Create your own Song");
        Tab trendingMusicTab = new Tab("Trending Music");
        Tab mySongsTab = new Tab("Your Creations");
        Tab likedSongsTab = new Tab("All you like");
        Tab profileTab = new Tab("Profile");

        Component createSongDashBoard = songPromptCard;
        Component trendingMusicDashboard = new TrendingMusicDashboard(songService, this.songUtilityService);
        Component mySongsDashBoard = new MySongsView(songService, userController);
        Component likedSongsDashBoard = new LikedSongsDashboard(songService, userController, songUtilityService);
        Component profileDashBoard = new ProfileDashboard();

        tabsToPages.put(createSongTab, createSongDashBoard);
        tabsToPages.put(trendingMusicTab, trendingMusicDashboard);
        tabsToPages.put(mySongsTab, mySongsDashBoard);
        tabsToPages.put(likedSongsTab, likedSongsDashBoard);
        tabsToPages.put(profileTab, profileDashBoard);

        tabs.add(createSongTab, trendingMusicTab, mySongsTab, likedSongsTab, profileTab);
        tabs.setFlexGrowForEnclosedTabs(1);

        // Add tab change listener
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            Component selectedContent = tabsToPages.get(selectedTab);
            if (selectedContent instanceof UpdatableTabContent) {
                ((UpdatableTabContent) selectedContent).updateContent();
            }
            removeAll(); // Clear existing components
            add(greeting, tabs, selectedContent); // Add tabs and the selected component
        });

        add(greeting, tabs); // Initially add greeting and tabs without content
        setSelectedTab(createSongTab); // Optionally set an initial tab
    }

    public interface UpdatableTabContent {
        void updateContent();
    }

    private void setSelectedTab(Tab tab) {
        Component content = tabsToPages.get(tab);
        add(content); // Add initial content below the tabs
    }
}
