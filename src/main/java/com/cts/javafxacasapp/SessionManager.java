package com.cts.javafxacasapp;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private String userRole;
    private String username;

    //adding collecting part searches for customer to session in an array
    private List<CustomerDashController.PartSearchEntry> recentSearches = new ArrayList<>();

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

    //storing part name searched and compatibitity info
    public List<CustomerDashController.PartSearchEntry> getRecentSearches() {
        return recentSearches;
    }

    public void addRecentSearch(CustomerDashController.PartSearchEntry entry) {
        recentSearches.add(0, entry);

        if (recentSearches.size() > 5) {
            recentSearches.remove(recentSearches.size() - 1);
        }
    }

    public static void clearSession() {
        if (instance != null) {
            instance.userRole = null;
            instance.username = null;
            instance.recentSearches.clear();
        }
    }
}