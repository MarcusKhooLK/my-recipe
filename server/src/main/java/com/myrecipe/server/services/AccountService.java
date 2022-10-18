package com.myrecipe.server.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myrecipe.server.EmailDetails;
import com.myrecipe.server.constants.URLs;
import com.myrecipe.server.repository.AccountRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private EmailService emailSvc;

    public boolean accountExists(String email) {
        return accRepo.userExists(email);
    }

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
                        A platform where you can search for simple recipes yet delicious dishes!</p>
                    <p>Have your own recipe to share around the world? Fred not, create your own custom recipe and share your love around!</p>
                    <a href="%s" style="
                    background-color: orange;
                    color: white;
                    padding: 15px 25px;
                    text-decoration: none;
                    cursor: pointer;
                    border: none;
                    border-radius: 10px;">Create your Recipe</a>
                    </div>
                    """.formatted(URLs.URL_HOME + "/#/account/recipe/create");
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
