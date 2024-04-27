package dev.dipesh.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import dev.dipesh.entity.Song;
import dev.dipesh.service.SongService;

import java.util.List;

import static dev.dipesh.gui.SecurityUtils.isUserLoggedIn;

@Route("")
@PageTitle("Music Dashboard")
@CssImport("./styles/default-dashboard-styles.css")
public class PublicDashboardView extends VerticalLayout {

    private final SongService songService; // Add your service here
    private HorizontalLayout linkContainer;
    private final SecuredViewAccessChecker accessChecker;

    public PublicDashboardView(SongService songService, SecuredViewAccessChecker accessChecker) {
        this.songService = songService; // Initialize your service
        this.accessChecker = accessChecker;
        addClassName("dashboard");

        // Header
        HorizontalLayout header = createHeader();
        // Menu
        VerticalLayout menu = createMenu();
        // Main content area
        HorizontalLayout mainContent = new HorizontalLayout(menu, createTrendingSection());
        mainContent.setSizeFull();
        mainContent.addClassName("main-content");
        // Footer
        HorizontalLayout footer = createFooter();

        // Initialize link container with navigation links
        initLinkContainer();

        // Adding components to the root layout
        add(header, mainContent, footer, linkContainer);
        setSizeFull();

        // Conditionally add the link container based on authentication
        updateVisibilityBasedOnAuthentication();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image logo = new Image("frontend/images/logo.png", "Logo");
        logo.setHeight("50px");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search...");
        searchField.addClassName("search-field");

        Button signInButton = new Button("Sign In", new Icon(VaadinIcon.SIGN_IN));
        signInButton.addClassName("sign-in-button");
        signInButton.addClickListener(event -> {
            // call your login method here
            UI.getCurrent().navigate(LoginView.class);
        });
        header.add(logo, searchField, signInButton);
        header.expand(searchField); // Makes the search bar expand
        return header;
    }


    private VerticalLayout createMenu() {
        VerticalLayout menu = new VerticalLayout();
        menu.addClassName("menu");
        menu.setWidth("200px");
        menu.setHeightFull();

        Button homeButton = new Button("Home", new Icon(VaadinIcon.HOME));
        Button searchButton = new Button("Search", new Icon(VaadinIcon.SEARCH));
        homeButton.setWidthFull();
        searchButton.setWidthFull();

        homeButton.addClickListener(event -> {
            // call your login method here
            UI.getCurrent().navigate(PublicDashboardView.class);

        });

        //Enter Song Title
        searchButton.addClickListener(event -> {
            // call your login method here
            //UI.getCurrent().navigate(MainView.class);

        });

        menu.add(homeButton, searchButton);
        return menu;
    }


    private HorizontalLayout createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("footer");
        footer.setWidth("100%");
        footer.setJustifyContentMode(JustifyContentMode.CENTER);

        Span copyright = new Span("© Echolodean");
        copyright.addClassName("footer-text");

        footer.add(copyright);
        return footer;
    }

    private VerticalLayout createTrendingSection() {
        VerticalLayout trendingLayout = new VerticalLayout();
        trendingLayout.addClassName("trending-section");
        trendingLayout.setWidthFull();

        // Fetch the trending songs using the songService
        List<Song> trendingSongs = songService.getTrendingSongs(10); // Adjust the number as needed

        for (Song song : trendingSongs) {
            HorizontalLayout songCard = createSongCard(song);
            trendingLayout.add(songCard);
        }

        return trendingLayout;
    }

    private HorizontalLayout createSongCard(Song song) {
        HorizontalLayout cardLayout = new HorizontalLayout();
        cardLayout.addClassName("song-card");
        cardLayout.setWidth("100%");
        cardLayout.setPadding(true);
        cardLayout.setAlignItems(Alignment.CENTER);

        Image albumCover = new Image(song.getImageUrl(), "Album cover");
        albumCover.setWidth("100px");
        albumCover.setHeight("100px");

        VerticalLayout songInfo = new VerticalLayout();
        songInfo.addClassName("song-info");
        songInfo.setPadding(false);
        songInfo.setSpacing(false);

        Span title = new Span(song.getTitle());
        title.addClassName("song-title");

        Span likes = new Span(String.valueOf(song) + " likes"); // Assuming you have a likes field
        likes.addClassName("song-likes");

        songInfo.add(title, likes);

        cardLayout.add(albumCover, songInfo);

        // You can add a click listener to each card if you want to perform an action when clicked
        // Attach a click listener to the card
        cardLayout.addClickListener(event -> {
            // Here, check if the user is logged in. This check depends on your authentication logic.
            // For example, if you use Spring Security, you can check if the user is authenticated.
            // If the user is not logged in, navigate to the WelcomeView.
            //if (!isUserLoggedIn()) { // Replace with your actual login check
            UI.getCurrent().navigate(LoginView.class);
            //} else {
            // If the user is logged in, perform the desired action, such as navigating to a detailed song view.
            // You could pass parameters to the detailed view using the navigate method as follows:
            UI.getCurrent().navigate(LoginView.class);
            //}
        });

        return cardLayout;
    }


    private void initLinkContainer() {
        // Router links
        RouterLink homeLink = new RouterLink("Home", UserDashboardView.class);
        RouterLink mySongsLink = new RouterLink("My Songs", MySongsView.class);
        // Styling links
        homeLink.addClassName("router-link");
        mySongsLink.addClassName("router-link");
        // Link container for layout control
        linkContainer = new HorizontalLayout(homeLink, mySongsLink);
        linkContainer.addClassName("link-container");
        linkContainer.setVisible(false); // Initially invisible, will be made visible after login
    }

    public void updateVisibilityBasedOnAuthentication() {
        boolean loggedIn = isUserLoggedIn();
        linkContainer.setVisible(loggedIn); // Show or hide the container based on login status
    }

}
