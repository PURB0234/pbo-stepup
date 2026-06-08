package com.models;

public class User {
    private int id;
    private String nama;
    private String email;
    private String nim;
    private String role;
    private String fotoProfile;
    private String password;

    public User(int id, String nama, String email, String nim, String role, String fotoProfile, String password) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.nim = nim;
        this.role = role;
        this.fotoProfile = fotoProfile;
        this.password = password;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFotoProfile() {
        return fotoProfile;
    }

    public void setFotoProfile(String fotoProfile) {
        this.fotoProfile = fotoProfile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
