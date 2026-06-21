package com.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.models.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service untuk operasi CRUD User via API PHP.
 */
public class UserService {

    /**
     * Mengambil semua data user dari API.
     * 
     * @return List user
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String response = ApiClient.sendGet("users_get.php");

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            if (json.get("success").getAsBoolean()) {
                JsonArray dataArray = json.getAsJsonArray("data");

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject obj = dataArray.get(i).getAsJsonObject();

                    User user = new User(
                            obj.get("id").getAsInt(),
                            obj.get("nama").getAsString(),
                            obj.get("email").getAsString(),
                            obj.get("role").getAsString(),
                            obj.has("foto_profile") && !obj.get("foto_profile").isJsonNull()
                                    ? obj.get("foto_profile").getAsString()
                                    : "",
                            "",
                            obj.has("status") && !obj.get("status").isJsonNull()
                                    ? obj.get("status").getAsString()
                                    : "active",
                            obj.has("poin") && !obj.get("poin").isJsonNull()
                                    ? obj.get("poin").getAsInt()
                                    : 0);

                    users.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Update role dan status user.
     * 
     * @return true jika berhasil
     */
    public static boolean updateUser(int id, String role, String status) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", String.valueOf(id));
        params.put("role", role);
        params.put("status", status);

        String response = ApiClient.sendPost("users_update.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hapus user berdasarkan ID.
     * 
     * @return true jika berhasil
     */
    public static boolean deleteUser(int id) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", String.valueOf(id));

        String response = ApiClient.sendPost("users_delete.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
