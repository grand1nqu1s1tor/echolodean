package dev.dipesh.controller;

import dev.dipesh.config.SpotifyConfiguration;
import dev.dipesh.service.SpotifyAuthorizationService;
import dev.dipesh.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.ParseException;
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
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
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
    SpotifyAuthorizationService spotifyAuthorizationService;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    @Autowired
    private UserService userService;


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

            // Process user details and implement user-specific logic if necessary
            dev.dipesh.entity.User localUser = processUserDetails(user);

            // Store user details in session
            session.setAttribute("user", localUser); // You may want to store a custom user object instead
            session.setAttribute("accessToken", authorizationCodeCredentials.getAccessToken());
            session.setAttribute("refreshToken", authorizationCodeCredentials.getRefreshToken());

            //Debug
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(authentication.getAuthorities());

            // Perform redirect
            response.sendRedirect(customIp + "/home");
        } catch (Exception e) { // Catch more general Exception to cover all bases
            System.out.println("Exception occurred while getting user code: " + e.getMessage());
            try {
                // Redirect to an error page or handle differently if needed
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
}
