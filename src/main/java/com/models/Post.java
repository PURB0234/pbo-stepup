package com.models;

/**
 * Model untuk data Post di Community Feed.
 * Sesuai dengan tabel 'community' di database.
 * Field: id, user_id, deskripsi, gambar, langkah, jarak, kalori, nama_user, foto_user
 */
public class Post {
    private int id;
    private int userId;
    private String deskripsi;
    private String gambar;
    private String langkah;
    private String jarak;
    private String kalori;
    private String namaUser;
    private String fotoUser;
    private String createdAt;

    // Constructor lengkap
    public Post(int id, int userId, String deskripsi, String gambar,
                String langkah, String jarak, String kalori,
                String namaUser, String fotoUser, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
        this.langkah = langkah;
        this.jarak = jarak;
        this.kalori = kalori;
        this.namaUser = namaUser;
        this.fotoUser = fotoUser;
        this.createdAt = createdAt;
    }

    // Constructor sederhana (tanpa gambar, langkah, jarak, kalori)
    public Post(int id, int userId, String deskripsi, String namaUser, String createdAt) {
        this(id, userId, deskripsi, "", "", "", "", namaUser, "", createdAt);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getLangkah() {
        return langkah;
    }

    public void setLangkah(String langkah) {
        this.langkah = langkah;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getKalori() {
        return kalori;
    }

    public void setKalori(String kalori) {
        this.kalori = kalori;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
