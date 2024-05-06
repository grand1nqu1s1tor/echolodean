package dev.dipesh.util;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.*;

public class SpotifyGenreUtils {
    public List<String> extractGenres(Paging<Artist> topArtists) {
        Set<String> genresSet = new HashSet<>();

        if (topArtists != null && topArtists.getItems() != null) {
            for (Artist artist : topArtists.getItems()) {
                if (artist.getGenres() != null) {
                    for (String genre : artist.getGenres()) {
                        genresSet.add(genre);
                    }
                }
            }
        }
        return pickTwoRandom(genresSet);
    }

    public static List<String> pickTwoRandom(Set<String> genres) {
        if(genres.size() == 1) return List.of(Arrays.toString(genres.toArray()));
        List<String> genreList = new ArrayList<>(genres);
        Random random = new Random();
        int firstIndex = random.nextInt(genreList.size());
        int secondIndex;
        do {
            secondIndex = random.nextInt(genreList.size());
        } while (firstIndex == secondIndex);
        return List.of(genreList.get(firstIndex), genreList.get(secondIndex));
    }
}
