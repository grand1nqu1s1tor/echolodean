package dev.dipesh.vaadin.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;

public class AudioPlayerComponent extends Div {

    private Element audioElement = new Element("audio");
    private Image albumCover = new Image();
    private Span titleSpan = new Span();
    private Span lyricsSpan = new Span();

    public AudioPlayerComponent() {
        audioElement.setAttribute("controls", true);

        // Wrap the audio element in a Div for better control over styling
        Div audioWrapper = new Div();
        audioWrapper.getElement().appendChild(audioElement);

        // Set up the initial visibility and styling for the album cover
        albumCover.setVisible(false);
        albumCover.getStyle()
                .set("maxWidth", "250px")
                .set("maxHeight", "250px");

        titleSpan.getStyle().set("font-weight", "bold"); // Styling for title

        lyricsSpan.getStyle()
                .set("white-space", "pre-wrap")
                .set("maxHeight", "150px")
                .set("overflowY", "auto") // Styling for lyrics
                .set("text-align", "center");

        // Add the album cover, title, audio wrapper, and lyrics to the layout
        this.add(albumCover, titleSpan, audioWrapper, lyricsSpan);
        this.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("padding", "10px");
    }

    public void setSource(String path) {
        audioElement.setProperty("src", path);
    }

    public void setLyrics(String lyrics) {
        lyricsSpan.setText(lyrics);
        lyricsSpan.setVisible(true); // Show the lyrics
    }

    public void setAlbumCover(String imageUrl) {
        albumCover.setSrc(imageUrl);
        albumCover.setVisible(!imageUrl.isEmpty());
    }

    public void setTitle(String title) {
        titleSpan.setText(title);
    }

    public void clear() {
        audioElement.removeProperty("src");
        lyricsSpan.setText("");
        albumCover.setSrc("");
        titleSpan.setText("");
        albumCover.setVisible(false);
        lyricsSpan.setVisible(false);
    }
}
