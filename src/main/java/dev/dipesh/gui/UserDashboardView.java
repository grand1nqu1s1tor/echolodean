package dev.dipesh.gui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("home")
@PageTitle("Home")
public class UserDashboardView extends VerticalLayout {

    private final TrendingMusicDashboard trendingMusicDashboard;


    @Autowired
    public UserDashboardView(TrendingMusicDashboard trendingMusicDashboard) {
        this.trendingMusicDashboard = trendingMusicDashboard;
        // User greeting
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        H1 greeting = new H1("Hello, " + userName + "!");

        Tabs tabs = new Tabs();
        Tab trendingSongs = new Tab(new RouterLink("Trending Songs", TrendingMusicDashboard.class));
        Tab mySongs = new Tab(new RouterLink("My Songs", MySongsView.class));
        Tab likedSongs = new Tab(new RouterLink("Liked Songs", LikedSongsDashboard.class));
        Tab profile = new Tab(new RouterLink("Profile", ProfileDashboard.class));

        Tab settings = new Tab("Settings");
        tabs.add(trendingSongs, mySongs, likedSongs, settings);

        // Adding components to the layout
        add(greeting, tabs, trendingMusicDashboard);
    }
}


