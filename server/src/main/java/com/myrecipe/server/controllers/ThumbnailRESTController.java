package com.myrecipe.server.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrecipe.server.services.MyRecipeService;
import com.myrecipe.server.services.S3Service;

@RestController
@RequestMapping(path = "/thumbnails", produces = MediaType.APPLICATION_JSON_VALUE)
public class ThumbnailRESTController {

    //@Autowired
    //private S3Service s3Svc;

    @Autowired
    private MyRecipeService myRecipeService;
    
    @GetMapping(path = "/{recipeId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String recipeId) {
        Optional<byte[]> thumbnailOpt = myRecipeService.getThumbnail(recipeId);

        if(thumbnailOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().headers(headers).body(thumbnailOpt.get());
        //return s3Svc.get(id);
    }
}
