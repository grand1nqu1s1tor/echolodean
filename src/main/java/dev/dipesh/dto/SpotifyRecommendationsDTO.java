package dev.dipesh.dto;

import java.util.List;

public class SpotifyRecommendationsDTO {
    private List<TrackDTO> tracks;
    public List<TrackDTO> getTracks() { return tracks; }
    public void setTracks(List<TrackDTO> tracks) { this.tracks = tracks; }
}

