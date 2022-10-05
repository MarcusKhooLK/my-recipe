package com.myrecipe.server.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class User {
    int userId;
    String username;
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
        return user;
    }
}
