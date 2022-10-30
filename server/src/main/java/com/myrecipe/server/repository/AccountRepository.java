package com.myrecipe.server.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.myrecipe.server.models.User;

@Repository
public class AccountRepository {
    
    private static final String SQL_INSERT_USER = "insert into user (username, email, password, newsletter) values (?, ?, sha1(?), ?);";
    private static final String SQL_AUTHORIZE_USER = "select * from user where email = ? and password = sha1(?);";
    private static final String SQL_SELECT_USER_BY_EMAIL = "select * from user where email = ?;";
    private static final String SQL_DELETE_USER = "delete from user where user_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createUser(String username, String email, String password) {
        return jdbcTemplate.update(SQL_INSERT_USER, username, email, password, true);
    }

    public Optional<User> authUser(String email, String password) {
        final SqlRowSet result = jdbcTemplate.queryForRowSet(SQL_AUTHORIZE_USER, email, password);
        if(result.next()) {
            return Optional.of(User.create(result));
        }
        return Optional.empty();
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

    public boolean deleteAccount(String email) {
        Optional<User> userOpt = findUserByEmail(email);
        if(userOpt.isEmpty()) {
            return false;
        }

        int result = jdbcTemplate.update(SQL_DELETE_USER, userOpt.get().getUserId());

        return result > 0;
    }
}
