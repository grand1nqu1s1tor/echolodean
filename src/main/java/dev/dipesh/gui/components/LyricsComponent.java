package dev.dipesh.gui.components;

import com.vaadin.flow.component.html.Div;

public class LyricsComponent extends Div {
    public LyricsComponent(String lyrics) {
        setText(lyrics);
        // Additional styling can be done here or via an external CSS file
        this.getStyle()
                .set("white-space", "pre-wrap") // Ensures that the lyrics are properly wrapped
                .set("max-height", "100px")     // Limits the height of the lyrics block
                .set("overflow-y", "auto");     // Adds a scrollbar if the content overflows
    }
}

