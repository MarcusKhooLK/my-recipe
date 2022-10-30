package com.myrecipe.server.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {
    private int userId;
    private String username;
    private boolean newsletter;
    private String email;
    private String sessionId = "";
    
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isNewsletter() {
        return newsletter;
    }
    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public static User create(final SqlRowSet rs) {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setNewsletter(rs.getBoolean("newsletter"));
        user.setEmail(rs.getString("email"));
        return user;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
        .add("userId", userId)
        .add("username", username)
        .add("newsletter", newsletter)
        .add("email", email)
        .add("sessionId", sessionId)
        .build();
    }
}
