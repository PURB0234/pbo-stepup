package com.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * HTTP Client helper untuk berkomunikasi dengan API PHP WEBPRO-STEPUP.
 * Menggunakan java.net.http.HttpClient bawaan Java 11+.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost/webpro-stepup/api/javafx/";
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Mengirim GET request ke endpoint API.
     * @param endpoint nama file PHP (contoh: "rewards_get.php")
     * @return response body sebagai String (JSON)
     */
    public static String sendGet(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"message\":\"Koneksi gagal: " + e.getMessage() + "\"}";
        }
    }

    /**
     * Mengirim POST request dengan form data ke endpoint API.
     * @param endpoint nama file PHP (contoh: "auth_login.php")
     * @param params Map berisi key-value form data
     * @return response body sebagai String (JSON)
     */
    public static String sendPost(String endpoint, Map<String, String> params) {
        try {
            // Build form data
            StringJoiner formData = new StringJoiner("&");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formData.add(
                        URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) +
                        "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData.toString()))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"message\":\"Koneksi gagal: " + e.getMessage() + "\"}";
        }
    }
}
