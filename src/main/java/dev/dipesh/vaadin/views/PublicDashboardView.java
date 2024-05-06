package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.entity.Song;
import dev.dipesh.vaadin.components.AudioPlayerComponent;
import dev.dipesh.vaadin.components.SongPromptComponent;
import dev.dipesh.service.SongService;
import dev.dipesh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import static dev.dipesh.vaadin.util.SecurityUtils.isUserLoggedIn;

@Route("")
@UIScope
@PageTitle("Music Dashboard")
@CssImport("./styles/default-dashboard-styles.css")
public class PublicDashboardView extends VerticalLayout {

    private final SongService songService;
    private final UserService userService;
    private HorizontalLayout linkContainer;
    private final SongPromptComponent songPromptComponent;
    private AudioPlayerComponent audioPlayerComponent;

    @Autowired
    public PublicDashboardView(SongService songService, UserService userService, SongPromptComponent songPromptComponent) {
        this.songService = songService;
        this.userService = userService;
        this.songPromptComponent = songPromptComponent;
        this.audioPlayerComponent = new AudioPlayerComponent();
        addClassName("dashboard");

        // Initialize layout components
        HorizontalLayout header = createHeader();
        VerticalLayout menu = createMenu();
        FlexLayout mainContent = new FlexLayout(createSongListLayout(), songPromptComponent, audioPlayerComponent);
        mainContent.setSizeFull();
        mainContent.addClassName("main-content");
        mainContent.setFlexDirection(FlexLayout.FlexDirection.ROW);

        // Add the audio player component
        mainContent.add(audioPlayerComponent);

        HorizontalLayout footer = createFooter();
        initLinkContainer();

        // Adding components to the root layout
        add(header, mainContent, footer, linkContainer);
        setSizeFull();

        // Update UI based on user authentication state
        updateVisibilityBasedOnAuthentication();
    }



    private VerticalLayout createSongListLayout() {
        VerticalLayout songListLayout = new VerticalLayout();
        songListLayout.addClassName("song-list");
        songListLayout.setSizeFull();
        songListLayout.getStyle().set("max-height", "100%").set("overflow-y", "auto");

        // Add a heading
        H2 songListHeading = new H2("Trending Songs");
        songListHeading.addClassName("section-heading");

        Image trendingImage = new Image("images/Trending.jpg", "Trending");
        trendingImage.setHeight("40px");

// Create a HorizontalLayout to keep the heading and image in one line
        HorizontalLayout headerLayout = new HorizontalLayout(songListHeading, trendingImage);
        headerLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER); // Aligns children vertically in the center
        headerLayout.setAlignItems(Alignment.CENTER); // Centers items along the cross axis

// Add the header layout to the song list layout or another parent layout
        songListLayout.add(headerLayout);

        Page<Song> trendingSongs = songService.getTrendingSongs(10);
        for (Song song : trendingSongs) {
            HorizontalLayout songCard = createSongCard(song);
            songListLayout.add(songCard);
        }

        return songListLayout;
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
            UI.getCurrent().navigate(LoginView.class);

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
        cardLayout.getStyle().set("cursor", "pointer"); // Make it clear that the card is clickable

        Image albumCover = new Image(song.getImageUrl() != null && !song.getImageUrl().isEmpty()
                ? song.getImageUrl() : "images/default_cover.png", "Album cover");
        albumCover.setWidth("100px");
        albumCover.setHeight("100px");

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.addClassName("info-layout");
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        Span title = new Span(song.getTitle());
        title.addClassName("song-title");

        Span artist = new Span("Artist Name: " + userService.getUserById(song.getUserId()).getUsername());

        infoLayout.add(title, artist);

        cardLayout.add(albumCover, infoLayout);
        cardLayout.addClickListener(event -> {
            audioPlayerComponent.setTitle(song.getTitle());
            audioPlayerComponent.setAlbumCover(song.getImageUrl());
            audioPlayerComponent.setSource(song.getAudioUrl());  // Ensure your Song entity has a getAudioUrl method
            audioPlayerComponent.setLyrics(song.getLyrics());
        });

        return cardLayout;
    }


    private void initLinkContainer() {

        // Router links
        RouterLink homeLink = new RouterLink("Home",UserDashboardView.class);
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
        linkContainer.setVisible(loggedIn);
    }

}
