package dev.dipesh.gui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

@Route("home")
@PageTitle("Home")
public class MainView extends VerticalLayout {

    private final TrendingMusicDashboard trendingMusicDashboard;


    @Autowired
    public MainView(TrendingMusicDashboard trendingMusicDashboard) {
        this.trendingMusicDashboard = trendingMusicDashboard;
        // User greeting
        String userName = "currentUser";  // TODO: Fetch from the security context
        H1 greeting = new H1("Hello, " + userName + "!");

        // Navigation menu
        Tabs tabs = new Tabs();
        Tab trending = new Tab(new RouterLink("Trending Music", TrendingMusicDashboard.class));
        Tab mySongs = new Tab(new RouterLink("My Songs", MySongsView.class));
        Tab settings = new Tab("Settings");
        tabs.add(trending, mySongs, settings);

        // Adding components to the layout
        add(greeting, tabs, trendingMusicDashboard);
    }
}


