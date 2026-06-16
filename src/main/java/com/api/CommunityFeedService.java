package com.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.models.Post;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service untuk operasi CRUD Community Feed via API PHP.
 * Menggunakan ApiClient untuk berkomunikasi dengan backend webpro-stepup.
 */
public class CommunityFeedService {

    /**
     * Mengambil semua post dari API, diurutkan dari terbaru.
     * @return List post
     */
    public static List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        String response = ApiClient.sendGet("community_feed_get.php");

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            if (json.get("success").getAsBoolean()) {
                JsonArray dataArray = json.getAsJsonArray("data");

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject obj = dataArray.get(i).getAsJsonObject();

                    Post post = new Post(
                            obj.get("id").getAsInt(),
                            obj.get("user_id").getAsInt(),
                            obj.has("deskripsi") && !obj.get("deskripsi").isJsonNull()
                                    ? obj.get("deskripsi").getAsString() : "",
                            obj.has("gambar") && !obj.get("gambar").isJsonNull()
                                    ? obj.get("gambar").getAsString() : "",
                            obj.has("langkah") && !obj.get("langkah").isJsonNull()
                                    ? obj.get("langkah").getAsString() : "",
                            obj.has("jarak") && !obj.get("jarak").isJsonNull()
                                    ? obj.get("jarak").getAsString() : "",
                            obj.has("kalori") && !obj.get("kalori").isJsonNull()
                                    ? obj.get("kalori").getAsString() : "",
                            obj.has("nama_user") && !obj.get("nama_user").isJsonNull()
                                    ? obj.get("nama_user").getAsString() : "Unknown",
                            obj.has("foto_user") && !obj.get("foto_user").isJsonNull()
                                    ? obj.get("foto_user").getAsString() : "",
                            obj.has("created_at") && !obj.get("created_at").isJsonNull()
                                    ? obj.get("created_at").getAsString() : ""
                    );

                    posts.add(post);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }

    /**
     * Membuat post baru via API.
     * @return true jika berhasil
     */
    public static boolean createPost(int userId, String deskripsi, String langkah, String jarak, String kalori) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("deskripsi", deskripsi);
        params.put("langkah", langkah);
        params.put("jarak", jarak);
        params.put("kalori", kalori);

        String response = ApiClient.sendPost("community_feed_post.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update post via API.
     * @return true jika berhasil
     */
    public static boolean updatePost(int postId, int userId, boolean isAdmin,
                                     String deskripsi, String langkah, String jarak, String kalori) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", String.valueOf(postId));
        params.put("user_id", String.valueOf(userId));
        params.put("is_admin", isAdmin ? "1" : "0");
        params.put("deskripsi", deskripsi);
        params.put("langkah", langkah);
        params.put("jarak", jarak);
        params.put("kalori", kalori);

        String response = ApiClient.sendPost("community_feed_update.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hapus post via API.
     * @return true jika berhasil
     */
    public static boolean deletePost(int postId, int userId, boolean isAdmin) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", String.valueOf(postId));
        params.put("user_id", String.valueOf(userId));
        params.put("is_admin", isAdmin ? "1" : "0");

        String response = ApiClient.sendPost("community_feed_delete.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mendapatkan pesan error/success dari response API terakhir.
     */
    public static String getResponseMessage(String response) {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("message").getAsString();
        } catch (Exception e) {
            return "Koneksi ke server gagal";
        }
    }
}
