
package dev.dipesh.service;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.io.IOException;

@Service
public class SpotifyAuthorizationService {

    private final SpotifyApi spotifyApi;

    @Autowired
    public SpotifyAuthorizationService(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    public void authorize(String code) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            // Storing access and refresh tokens using SpotifyApi object
            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            System.out.println("Access Token Expires in: " + credentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }


    public String refreshAccessToken() throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh().build();
        AuthorizationCodeCredentials credentials = refreshRequest.execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return credentials.getAccessToken();
    }

}

