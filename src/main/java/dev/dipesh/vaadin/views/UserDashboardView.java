package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.dipesh.controller.UserController;
import dev.dipesh.vaadin.components.SongPromptComponent;
import dev.dipesh.vaadin.util.SongUtilityService;
import dev.dipesh.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Route("home")
@PageTitle("Home")
@CssImport("./styles/default-dashboard-styles.css")
public class UserDashboardView extends VerticalLayout {

    private Tabs tabs = new Tabs();
    private Map<Tab, Component> tabsToPages = new HashMap<>();
    private final SongService songService;
    private final UserController userController;
    private final SongUtilityService songUtilityService;


    @Autowired
    public UserDashboardView(SongPromptComponent songPromptComponent, SongService songService, UserController userController, SongUtilityService songUtilityService) {
        this.userController = userController;
        this.songService = songService;
        this.songUtilityService = songUtilityService;

        H1 greeting = new H1("Hello, " + userController.getCurrentUser().getUsername() + "!");
        add(greeting);

        setSizeFull();

        Tab createSongTab = new Tab("Create your own Song");
        Tab trendingMusicTab = new Tab("Trending Music");
        Tab mySongsTab = new Tab("Your Creations");
        Tab likedSongsTab = new Tab("All you like");
        Tab profileTab = new Tab("Profile");

        //Apply Styling
        createSongTab.addClassName("tab-style");
        trendingMusicTab.addClassName("tab-style");
        mySongsTab.addClassName("tab-style");
        likedSongsTab.addClassName("tab-style");
        profileTab.addClassName("tab-style");

        Component createSongDashBoard = songPromptComponent;
        Component trendingMusicDashboard = new TrendingDashboardView(songService, this.songUtilityService);
        Component mySongsDashBoard = new MySongsView(songService, userController);
        Component likedSongsDashBoard = new LikedSongsDashboardView(songService, userController, songUtilityService);
        Component profileDashBoard = new ProfileDashboardView();

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
            removeAll();
            add(greeting, tabs, selectedContent);
        });

        addClassName("user-dashboard");

        add(greeting, tabs);
        setSelectedTab(createSongTab);
    }

    public interface UpdatableTabContent {
        void updateContent();
    }

    private void setSelectedTab(Tab tab) {
        Component content = tabsToPages.get(tab);
        add(content);
    }
}
