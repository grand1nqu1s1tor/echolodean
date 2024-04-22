package dev.dipesh.service;

import dev.dipesh.config.SpotifyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.time.LocalDateTime;

@Service
public class SpotifyTokenService {

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiryTime;

    public synchronized String getAccessToken() throws Exception {
        if (accessToken == null || LocalDateTime.now().isAfter(tokenExpiryTime)) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void refreshAccessToken() throws Exception {
        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
        if (refreshToken == null) {
            throw new IllegalStateException("No refresh token available");
        }
        try {
            AuthorizationCodeRefreshRequest request = spotifyApi.authorizationCodeRefresh().build();
            AuthorizationCodeCredentials credentials = request.execute();
            this.accessToken = credentials.getAccessToken();
            this.refreshToken = credentials.getRefreshToken();
            this.tokenExpiryTime = LocalDateTime.now().plusSeconds(credentials.getExpiresIn());
        } catch (Exception e) {
            throw new Exception("Failed to refresh Spotify access token", e);
        }
    }

    public void setInitialTokens(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiryTime = LocalDateTime.now().plusSeconds(expiresIn);
    }
}
