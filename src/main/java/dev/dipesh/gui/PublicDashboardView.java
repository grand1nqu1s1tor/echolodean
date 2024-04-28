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
import dev.dipesh.service.UserService;
import org.springframework.data.domain.Page;

import static dev.dipesh.gui.SecurityUtils.isUserLoggedIn;

@Route("")
@PageTitle("Music Dashboard")
@CssImport("./styles/default-dashboard-styles.css")
public class PublicDashboardView extends VerticalLayout {

    private final SongService songService;
    private final UserService userService;
    private HorizontalLayout linkContainer;
    private final SecuredViewAccessChecker accessChecker;

    public PublicDashboardView(SongService songService, UserService userService, SecuredViewAccessChecker accessChecker) {
        this.songService = songService; // Initialize your service
        this.userService = userService;
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

        Image logo = new Image("images/logo.png", "Logo");
        logo.setHeight("100px");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search...");
        searchField.addClassName("search-field");

        Button signInButton = new Button("Sign In", new Icon(VaadinIcon.SIGN_IN));
        signInButton.addClassName("sign-in-button");
        signInButton.addClickListener(event -> {
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
        Page<Song> trendingSongs = songService.getTrendingSongs(10); // Adjust the number as needed

        for (Song song : trendingSongs) {
            HorizontalLayout songCard = createSongCard(song);
            trendingLayout.add(songCard);
        }

        return trendingLayout;
    }

    private HorizontalLayout createSongCard(Song song) {
        HorizontalLayout cardLayout = new HorizontalLayout();
        cardLayout.addClassName("song-card");
        cardLayout.setWidthFull();
        cardLayout.setAlignItems(Alignment.CENTER);
        cardLayout.setJustifyContentMode(JustifyContentMode.START);
        cardLayout.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        cardLayout.getStyle().set("border-radius", "var(--lumo-border-radius)");
        cardLayout.getStyle().set("padding", "var(--lumo-space-m)");
        cardLayout.getStyle().set("margin", "var(--lumo-space-m)");
        cardLayout.getStyle().set("box-shadow", "var(--lumo-box-shadow-xs)");


        Image albumCover = song.getImageUrl() != null && !song.getImageUrl().isEmpty()
                ? new Image(song.getImageUrl(), "Album cover")
                : new Image("images/default_cover.png", "Default album cover");
        albumCover.setWidth("100px");
        albumCover.setHeight("100px");

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.addClassName("info-layout");
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        Span title = new Span(song.getTitle());
        title.addClassName("song-title");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");

        Span artist = new Span("Artist Name: " + userService.getUserById(song.getUserId()).getUsername() ); // Replace with the actual artist name if available
        artist.addClassName("song-artist");

        Button playButton = new Button(new Icon(VaadinIcon.PLAY));
        playButton.addClassName("play-button");
        // Add click listener for playButton if needed

        infoLayout.add(title, artist, playButton);

        cardLayout.add(albumCover, infoLayout);

        cardLayout.addClickListener(event -> {
            UI.getCurrent().navigate(LoginView.class);
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
        linkContainer.setVisible(false);
    }

    public void updateVisibilityBasedOnAuthentication() {
        boolean loggedIn = isUserLoggedIn();
        linkContainer.setVisible(loggedIn); // Show or hide the container based on login status
    }

}
