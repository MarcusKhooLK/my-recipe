package com.myrecipe.server.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myrecipe.server.repository.AccountRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accRepo;

    public int createAccount(String username, String email, String password) {
        if(accRepo.userExists(email)) {
            return -1;
        }
        return accRepo.createUser(username, email, password);
    }

    public Map<String, Object> authAccount(String email, String password) {
        return accRepo.authUser(email, password);
    }
}
