package com.myrecipe.server.controllers;

import java.io.StringReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrecipe.server.models.Response;
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

    @PostMapping(path="/auth")
    public ResponseEntity<String> authAccount(@RequestBody String payload) {
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        Map<String, Object> user = accSvc.authAccount(jObj.getString("email"), jObj.getString("password"));
        Response r = new Response();
        if(!user.isEmpty()) {
            String email = (String)user.get("email");
            String username = (String)user.get("username");
            r.setCode(HttpStatus.OK.value());
            r.setMessage("Account OK");
            JsonObject data = Json.createObjectBuilder().add("email", email).add("username", username).build();
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
        Map<String, Object> user = accSvc.authAccount(jObj.getString("email"), jObj.getString("token"));
        Response r = new Response();
        if(!user.isEmpty()) {
            String email = (String)user.get("email");
            String username = (String)user.get("username");
            r.setCode(HttpStatus.OK.value());
            r.setMessage("Account OK");
            JsonObject data = Json.createObjectBuilder().add("email", email).add("username", username).build();
            r.setData(data);
            return ResponseEntity.ok().body(r.toJson().toString());
        }else {
            int result = accSvc.createAccount(jObj.getString("username"), jObj.getString("email"), jObj.getString("token"));
            if(result > 0) {
                r.setCode(HttpStatus.OK.value());
                r.setMessage("Account OK");
                JsonObject data = Json.createObjectBuilder().add("email", jObj.getString("email")).add("username", jObj.getString("username")).build();
                r.setData(data);
                return ResponseEntity.ok().body(r.toJson().toString());
            } else {
                r.setCode(HttpStatus.FORBIDDEN.value());
                r.setMessage("Forbidden");
                return ResponseEntity.status(r.getCode()).body(r.toJson().toString());
            }
        }
    }
}
