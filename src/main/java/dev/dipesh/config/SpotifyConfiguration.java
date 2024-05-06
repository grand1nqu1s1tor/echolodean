package dev.dipesh.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Configuration
public class SpotifyConfiguration {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${custom.server.ip}")
    private String customServerIp;

    @Bean
    public URI spotifyRedirectUri() {
        return SpotifyHttpManager.makeUri(customServerIp + "/api/get-user-code");
    }

    @Bean
    public SpotifyApi getSpotifyObject() {
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(spotifyRedirectUri())
                .build();
    }

    public SpotifyApi getSpotifyObjectFromSession(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");

        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(spotifyRedirectUri())
                .build();
    }
}
