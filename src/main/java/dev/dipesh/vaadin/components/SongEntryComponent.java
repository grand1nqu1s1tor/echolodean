package dev.dipesh.vaadin.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dev.dipesh.entity.Song;

public class SongEntryComponent extends HorizontalLayout {

    private Song song;
    private Image coverImage;
    private Span title;
    private Span lyrics;
    private Button playButton;

    public SongEntryComponent(Song song, Runnable playAction) {
        this.song = song;
        setWidthFull();
        setAlignItems(Alignment.CENTER);

        coverImage = new Image(song.getImageUrl(), "Album cover");
        coverImage.setHeight("64px");

        title = new Span(song.getTitle());
        title.getStyle().set("font-weight", "bold");

        lyrics = new Span(song.getLyrics());
        lyrics.getStyle().set("color", "grey");

        playButton = new Button("Play", event -> playAction.run());

        add(coverImage, title, lyrics, playButton);
    }
}
