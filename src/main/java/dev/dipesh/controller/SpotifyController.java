package dev.dipesh.controller;

import dev.dipesh.config.SpotifyConfiguration;
import dev.dipesh.service.SpotifyAuthorizationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

@RestController
@RequestMapping("/api")
public class SpotifyController {

    @Value("${custom.server.ip}")
    private String customIp;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    SpotifyAuthorizationService spotifyAuthorizationService;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    static HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @GetMapping("login")
    public void spotifyLogin(HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-read")
                .show_dialog(true)
                .build();

        final URI uri = authorizationCodeUriRequest.execute();
        response.sendRedirect(uri.toString());
    }

    @GetMapping("/get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, RedirectAttributes redirectAttributes) {
        try {
            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
            spotifyAuthorizationService.authorize(userCode);
            GetCurrentUsersProfileRequest profileRequest = spotifyApi.getCurrentUsersProfile().build();
            User user = profileRequest.execute();
            userProfileService.insertOrUpdateUserDetails(user, spotifyApi.getAccessToken(), spotifyApi.getRefreshToken());
            return "redirect:" + customIp + "/home?id=" + user.getId();
        } catch (Exception e) {
            System.out.println("Exception occurred while getting user code: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error processing Spotify data");
            return "redirect:/error";
        }
    }

}
