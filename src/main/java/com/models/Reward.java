package com.models;

/**
 * Model untuk data Reward.
 * Sesuai dengan tabel 'rewards' di database.
 */
public class Reward {
    private int idReward;
    private String nameReward;
    private String description;
    private int poin;
    private String gambar;
    private int stok;

    public Reward(int idReward, String nameReward, String description, int poin, String gambar) {
        this(idReward, nameReward, description, poin, gambar, 0);
    }

    public Reward(int idReward, String nameReward, String description, int poin, String gambar, int stok) {
        this.idReward = idReward;
        this.nameReward = nameReward;
        this.description = description;
        this.poin = poin;
        this.gambar = gambar;
        this.stok = stok;
    }

    // Getters and Setters
    public int getIdReward() {
        return idReward;
    }

    public void setIdReward(int idReward) {
        this.idReward = idReward;
    }

    public String getNameReward() {
        return nameReward;
    }

    public void setNameReward(String nameReward) {
        this.nameReward = nameReward;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}
