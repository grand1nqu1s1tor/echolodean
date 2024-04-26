package dev.dipesh.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
@Configuration
public class SpotifyConfiguration {

	@Value("${spotify.client-id}")
	private String clientId;

	@Value("${spotify.client-secret}")
	private String clientSecret;

	@Value("${redirect.server.ip}")
	private String redirectServerIp;

	@Bean
	public SpotifyApi getSpotifyObject() {
		URI redirectUri = SpotifyHttpManager.makeUri(redirectServerIp + "/api/get-user-code");

		return new SpotifyApi.Builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setRedirectUri(redirectUri)
				.build();
	}
}
