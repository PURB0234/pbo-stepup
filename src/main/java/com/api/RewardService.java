package com.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.models.Reward;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service untuk operasi CRUD Reward via API PHP.
 */
public class RewardService {

    /**
     * Mengambil semua data reward dari API.
     * @return List reward
     */
    public static List<Reward> getAllRewards() {
        List<Reward> rewards = new ArrayList<>();

        String response = ApiClient.sendGet("rewards_get.php");

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            if (json.get("success").getAsBoolean()) {
                JsonArray dataArray = json.getAsJsonArray("data");

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject obj = dataArray.get(i).getAsJsonObject();

                    Reward reward = new Reward(
                            obj.get("id_reward").getAsInt(),
                            obj.get("name_reward").getAsString(),
                            obj.has("description") && !obj.get("description").isJsonNull()
                                    ? obj.get("description").getAsString() : "",
                            obj.get("poin").getAsInt(),
                            obj.has("gambar") && !obj.get("gambar").isJsonNull()
                                    ? obj.get("gambar").getAsString() : "",
                            obj.has("stok") && !obj.get("stok").isJsonNull()
                                    ? obj.get("stok").getAsInt() : 0);

                    rewards.add(reward);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rewards;
    }

    /**
     * Tambah reward baru dengan stok default 0.
     * @return true jika berhasil
     */
    public static boolean addReward(String nameReward, int poin, String description) {
        return addReward(nameReward, poin, description, 0);
    }

    /**
     * Tambah reward baru dengan stok.
     * @return true jika berhasil
     */
    public static boolean addReward(String nameReward, int poin, String description, int stok) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name_reward", nameReward);
        params.put("poin", String.valueOf(poin));
        params.put("description", description);
        params.put("stok", String.valueOf(stok));

        String response = ApiClient.sendPost("rewards_post.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hapus reward berdasarkan ID.
     * @return true jika berhasil
     */
    public static boolean deleteReward(int id) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", String.valueOf(id));

        String response = ApiClient.sendPost("rewards_delete.php", params);

        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.get("success").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Melakukan redeem reward untuk user.
     * @return JsonObject response dari API
     */
    public static JsonObject redeemReward(int userId, int rewardId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("user_id", String.valueOf(userId));
        params.put("reward_id", String.valueOf(rewardId));

        String response = ApiClient.sendPost("rewards_redeem.php", params);

        try {
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("success", false);
            errorJson.addProperty("message", "Gagal menghubungi server");
            return errorJson;
        }
    }

    /**
     * Mengambil riwayat redeem untuk user.
     * @return JsonObject response dari API
     */
    public static JsonObject getRedemptionHistory(int userId) {
        String response = ApiClient.sendGet("rewards_history.php?user_id=" + userId);

        try {
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("success", false);
            errorJson.addProperty("message", "Gagal menghubungi server");
            return errorJson;
        }
    }
}
