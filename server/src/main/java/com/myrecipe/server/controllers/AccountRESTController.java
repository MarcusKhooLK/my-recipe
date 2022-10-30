package com.myrecipe.server.controllers;

import java.io.StringReader;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrecipe.server.models.Response;
import com.myrecipe.server.models.User;
import com.myrecipe.server.services.AccountService;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path="/api/account", produces=MediaType.APPLICATION_JSON_VALUE)
public class AccountRESTController {

    @Autowired
    private AccountService accSvc;
    
    @PostMapping()
    public ResponseEntity<String> createAccount(@RequestBody String payload) {
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        int result = accSvc.createAccount(jObj.getString("username"), jObj.getString("email"), jObj.getString("password"));
        Response r = new Response();
        if(result> 0) { 
            r.setCode(HttpStatus.CREATED.value());
            r.setMessage("Account created");
            return ResponseEntity.ok().body(r.toJson().toString());
        }else {
            r.setCode(HttpStatus.BAD_REQUEST.value());
            r.setMessage("Email already exists.");
            return ResponseEntity.badRequest().body(r.toJson().toString());
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteAccount(@RequestBody String email) {
        boolean result = accSvc.deleteAccount(email);
        Response r = new Response();
        if(result) { 
            r.setCode(HttpStatus.OK.value());
            r.setMessage("Account deleted");
            return ResponseEntity.ok().body(r.toJson().toString());
        }else {
            r.setCode(HttpStatus.BAD_REQUEST.value());
            r.setMessage("Oops! Something went wrong when deleting account!");
            return ResponseEntity.badRequest().body(r.toJson().toString());
        }
    }

    @PostMapping(path="/auth")
    public ResponseEntity<String> authAccount(@RequestBody String payload) {
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        Optional<User> userOpt = accSvc.authAccount(jObj.getString("email"), jObj.getString("password"));
        Response r = new Response();
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            r.setCode(HttpStatus.OK.value());
            r.setMessage("Account OK");
            JsonObject data = user.toJson();
            r.setData(data);
            return ResponseEntity.ok().body(r.toJson().toString());
        }else {
            r.setCode(HttpStatus.FORBIDDEN.value());
            r.setMessage("Forbidden");
            return ResponseEntity.status(r.getCode()).body(r.toJson().toString());
        }
    }

    @PostMapping(path="/authsocial")
    public ResponseEntity<String> authSocial(@RequestBody String payload) {
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        Optional<User> userOpt = accSvc.authAccount(jObj.getString("email"), jObj.getString("token"));
        Response r = new Response();
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            r.setCode(HttpStatus.OK.value());
            r.setMessage("Account OK");
            JsonObject data = user.toJson();
            r.setData(data);
            return ResponseEntity.ok().body(r.toJson().toString());
        } else {
            int result = accSvc.createAccount(jObj.getString("username"), jObj.getString("email"), jObj.getString("token"));
            if(result > 0) {
                userOpt = accSvc.authAccount(jObj.getString("email"), jObj.getString("token"));
                if(userOpt.isPresent()) {
                    User user = userOpt.get();
                    r.setCode(HttpStatus.OK.value());
                    r.setMessage("Account OK");
                    JsonObject data = user.toJson();
                    r.setData(data);
                    return ResponseEntity.ok().body(r.toJson().toString());
                } else {
                    r.setCode(HttpStatus.UNAUTHORIZED.value());
                    r.setMessage("Something went wrong when auth social account!");
                    return ResponseEntity.status(r.getCode()).body(r.toJson().toString());
                }
            } else {
                r.setCode(HttpStatus.UNAUTHORIZED.value());
                r.setMessage("Account already exists");
                return ResponseEntity.status(r.getCode()).body(r.toJson().toString());
            }
        }
    }

    @PostMapping(path="/authsession")
    public ResponseEntity<String> authSession(@RequestBody String payload) {
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        final String sessionId = jObj.getString("sessionId");
        Optional<User> userOpt = accSvc.authSession(sessionId);
        Response resp = new Response();
        if(userOpt.isPresent()) {
            resp.setCode(HttpStatus.OK.value());
            resp.setData(userOpt.get().toJson());
            resp.setMessage("Session auth successful");
            return ResponseEntity.ok().body(resp.toJson().toString());
        } else {
            resp.setCode(HttpStatus.UNAUTHORIZED.value());
            resp.setMessage("Invalid session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp.toJson().toString());
        }
    }

    @DeleteMapping(path="/authsession")
    public ResponseEntity<String> deleteSession(@RequestBody String sessionId) {
        accSvc.removeSession(sessionId);
        return ResponseEntity.ok().build();
    }
}
