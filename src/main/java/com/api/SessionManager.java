package com.api;

import com.models.User;

/**
 * Singleton untuk menyimpan data user yang sedang login.
 * Digunakan di seluruh aplikasi untuk mengecek role dan mendapatkan info user.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public void logout() {
        currentUser = null;
    }
}
