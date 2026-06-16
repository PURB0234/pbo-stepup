package com.api;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.models.User;

/**
 * Service untuk autentikasi (login & register) via API PHP.
 */
public class AuthService {

    /**
     * Login user via API.
     * @return User object jika berhasil, null jika gagal
     */
    public static User login(String email, String password) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("email", email);
        params.put("password", password);

        String response = ApiClient.sendPost("auth_login.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            if (json.get("success").getAsBoolean()) {
                JsonObject data = json.getAsJsonObject("data");

                User user = new User(
                        data.get("id").getAsInt(),
                        data.get("nama").getAsString(),
                        data.get("email").getAsString(),
                        data.has("nim") && !data.get("nim").isJsonNull()
                                ? data.get("nim").getAsString() : "",
                        data.get("role").getAsString(),
                        data.has("foto_profile") && !data.get("foto_profile").isJsonNull()
                                ? data.get("foto_profile").getAsString() : "",
                        "",
                        data.has("status") && !data.get("status").isJsonNull()
                                ? data.get("status").getAsString() : "active",
                        data.has("poin") && !data.get("poin").isJsonNull()
                                ? data.get("poin").getAsInt() : 0);

                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mendapatkan pesan error dari response login terakhir.
     */
    public static String getLoginError(String email, String password) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("email", email);
        params.put("password", password);

        String response = ApiClient.sendPost("auth_login.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("message").getAsString();
        } catch (Exception e) {
            return "Koneksi ke server gagal";
        }
    }

    /**
     * Register user baru via API.
     * @return true jika berhasil
     */
    public static boolean register(String nama, String email, String password, String nim) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("nama", nama);
        params.put("email", email);
        params.put("password", password);
        params.put("nim", nim);

        String response = ApiClient.sendPost("auth_register.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mendapatkan pesan dari response register.
     */
    public static String getRegisterMessage(String nama, String email, String password, String nim) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("nama", nama);
        params.put("email", email);
        params.put("password", password);
        params.put("nim", nim);

        String response = ApiClient.sendPost("auth_register.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("message").getAsString();
        } catch (Exception e) {
            return "Koneksi ke server gagal";
        }
    }
}
