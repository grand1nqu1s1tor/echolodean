package dev.dipesh.util;

public class ApiUrlConstants {
    // Base URL
    public static final String BASE_URL = "https://api.sunoaiapi.com/api/v1/";

    // Specific API paths
    public static final String GET_SONG = BASE_URL + "gateway/feed/";
    public static final String GENERATE_SONG = BASE_URL + "gateway/generate/gpt_desc";
    public static final String REDIRECT_URI = "http://localhost:8080/api/get-user-code";

}
