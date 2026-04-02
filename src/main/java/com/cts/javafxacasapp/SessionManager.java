package com.cts.javafxacasapp;

public class SessionManager {

    private static SessionManager instance;
    private String userRole;
    private String username;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public static void clearSession() {
        if (instance != null) {
            instance.userRole = null;
            instance.username = null;
        }
    }
}