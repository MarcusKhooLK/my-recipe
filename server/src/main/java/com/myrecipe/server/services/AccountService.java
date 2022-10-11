package com.myrecipe.server.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myrecipe.server.EmailDetails;
import com.myrecipe.server.repository.AccountRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private EmailService emailSvc;

    public int createAccount(String username, String email, String password) {
        if(accRepo.userExists(email)) {
            return -1;
        }
        int result = accRepo.createUser(username, email, password);
        if(result == 1) {
            String msgBody = """
                    <div style="text-align:center;">
                    <h1>Welcome to MyRecipe!</h1>
                    <p>Thank you for joining MyRecipe, a platform to connect like-minded cooks like you! 
                        A platform where you can search for simple recipes yet delicious dishes! 
                        Have your own recipe to share around the world? 
                        Fred not, click below to create your own custom recipe and share your love around!
                    </p>
                    <a href="">Create your Recipe</a>
                    </div>
                    """;
            String subject = "Welcome to MyRecipe, " + username + "!";
            EmailDetails details = new EmailDetails(email, msgBody, subject);
            emailSvc.sendEmail(details);
        }
        return result;
    }

    public Map<String, Object> authAccount(String email, String password) {
        return accRepo.authUser(email, password);
    }
}
