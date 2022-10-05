package com.myrecipe.server.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.myrecipe.server.models.User;

@Repository
public class AccountRepository {
    
    private static final String SQL_INSERT_USER = "insert into user (username, email, password) values (?, ?, sha1(?));";
    private static final String SQL_AUTHORIZE_USER = "select * from user where email = ? and password = sha1(?);";
    private static final String SQL_SELECT_USER_BY_EMAIL = "select * from user where email = ?;";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createUser(String username, String email, String password) {
        return jdbcTemplate.update(SQL_INSERT_USER, username, email, password);
    }

    public Map<String, Object> authUser(String email, String password) {
        final SqlRowSet result = jdbcTemplate.queryForRowSet(SQL_AUTHORIZE_USER, email, password);
        if(result.next()) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", result.getString("email"));
            hashMap.put("username", result.getString("username"));
            return hashMap;
        }
        return new HashMap<>();
    }

    public boolean userExists(String email) {
        final SqlRowSet result = jdbcTemplate.queryForRowSet(SQL_SELECT_USER_BY_EMAIL, email);
        return result.next();
    }

    public Optional<User> findUserByEmail(String email) {
        final SqlRowSet result = jdbcTemplate.queryForRowSet(SQL_SELECT_USER_BY_EMAIL, email);
        if(result.next()) {
            return Optional.of(User.create(result));
        } else {
            return Optional.empty();
        }
    }
}
