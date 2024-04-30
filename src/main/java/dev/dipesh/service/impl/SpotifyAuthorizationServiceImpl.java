package dev.dipesh.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.io.IOException;

@Service
public class SpotifyAuthorizationServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(SpotifyAuthorizationServiceImpl.class);
    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyAuthorizationServiceImpl(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public void authorize(String code) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            log.info("Access Token Expires in: {}", credentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            log.error("Authorization failed", e);
            throw new RuntimeException("Failed to authorize with Spotify", e);
        }
    }

    public String refreshAccessToken() {
        try {
            AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh().build();
            AuthorizationCodeCredentials credentials = refreshRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
            return credentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            log.error("Failed to refresh access token", e);
            throw new RuntimeException("Failed to refresh access token", e);
        }
    }
}


