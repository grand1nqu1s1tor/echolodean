package dev.dipesh.controller;

import dev.dipesh.config.SpotifyConfiguration;
import dev.dipesh.service.UserService;
import dev.dipesh.service.impl.SpotifyAuthorizationServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
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
    SpotifyAuthorizationServiceImpl spotifyAuthorizationServiceImpl;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    @Autowired
    private UserService userService;

    //TODO remove after testing
    @Autowired
    private HttpSession session;


    @Autowired
    static HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @GetMapping("login")
    public void spotifyLogin(HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-read-email user-read-private user-read-playback-state user-top-read user-library-read playlist-read-private playlist-read-collaborative user-follow-read").show_dialog(true)
                .build();

        final URI uri = authorizationCodeUriRequest.execute();
        response.sendRedirect(uri.toString());
    }


    @GetMapping("/get-user-code")
    public void getSpotifyUserCode(@RequestParam("code") String userCode, RedirectAttributes redirectAttributes, HttpServletResponse response, HttpSession session) {
        try {
            SpotifyApi spotifyApi = spotifyConfiguration.getSpotifyObject();
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            GetCurrentUsersProfileRequest profileRequest = spotifyApi.getCurrentUsersProfile().build();
            User user = profileRequest.execute();
            dev.dipesh.entity.User localUser = processUserDetails(user);

            final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                    .build();

            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            System.out.println("U" + artistPaging.toString());

            // Store user details in session
            session.setAttribute("user", localUser); // You may want to store a custom user object instead
            session.setAttribute("accessToken", authorizationCodeCredentials.getAccessToken());
            session.setAttribute("refreshToken", authorizationCodeCredentials.getRefreshToken());

            // Perform redirect
            response.sendRedirect(customIp + "/home");
        } catch (Exception e) {
            System.out.println("Exception occurred while getting user code: " + e.getMessage());
            try {
                response.sendRedirect("/error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private dev.dipesh.entity.User processUserDetails(User spotifyUser) {
        dev.dipesh.entity.User localUser = userService.getUserById(spotifyUser.getId());
        if (localUser == null) {
            localUser = new dev.dipesh.entity.User();
            localUser.setUserId(spotifyUser.getId());
            localUser.setUsername(spotifyUser.getDisplayName());
            localUser.setEmail(spotifyUser.getEmail());
        }
        userService.saveUser(localUser);
        return localUser;
    }


    void testMethod() {
        try {
            // Retrieve the access token from the session
            String accessToken = (String) session.getAttribute("accessToken");

            // Construct a SpotifyApi object using the access token
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setAccessToken(accessToken)
                    .build();

            // Make the desired API call using the SpotifyApi object
            final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
//          .limit(10)
//          .offset(0)
//          .time_range("medium_term")
                    .build();

            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            System.out.println("U" + artistPaging.toString());
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        }
    }


}
