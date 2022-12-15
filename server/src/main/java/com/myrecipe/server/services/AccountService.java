package com.myrecipe.server.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrecipe.server.EmailDetails;
import com.myrecipe.server.constants.EmailTemplate;
import com.myrecipe.server.constants.URLs;
import com.myrecipe.server.models.RecipeSummary;
import com.myrecipe.server.models.User;
import com.myrecipe.server.repository.AccountRepository;
import com.myrecipe.server.repository.SessionRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private MyRecipeService recipeSvc;

    @Autowired
    private SessionRepository sessionRepo;

    @Autowired
    private S3Service s3Svc;

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
            String msgBody = EmailTemplate.constructWelcome(URLs.URL_HOME + "/#/account/recipe/create");
            String subject = "Welcome to MyRecipe, " + username + "!";
            EmailDetails details = new EmailDetails(email, msgBody, subject);
            emailSvc.sendEmail(details);
        }
        return result;
    }

    public Optional<User> authAccount(String email, String password) {
        Optional<User> userOpt = accRepo.authUser(email, password);
        if(userOpt.isPresent()) {
            // generate token
            final String uuid = UUID.randomUUID().toString().substring(0, 8);
            sessionRepo.setSession(uuid, email);
            userOpt.get().setSessionId(uuid);
        }
        return userOpt;
    }

    public Optional<User> authSession(final String sessionId) {
        boolean result = sessionRepo.hasSession(sessionId);
        if(result) {
            String email = sessionRepo.getValue(sessionId);
            return accRepo.findUserByEmail(email);
        } else {
            return Optional.empty();
        }
    }

    public void removeSession(final String sessionId) {
        sessionRepo.removeSession(sessionId);
    }

    @Transactional
    public boolean deleteAccount(String email) {
        sessionRepo.removeSession(email);
        List<RecipeSummary> recipes = recipeSvc.getRecipesSummaryByEmail(email);
        recipes.forEach(v->{
            recipeSvc.deleteRecipeByRecipeId(v.getRecipeId());
                //s3Svc.delete(v.getThumbnail());
        });

        return accRepo.deleteAccount(email);
    }
}
