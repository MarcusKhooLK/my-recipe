package com.myrecipe.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrecipe.server.services.S3Service;

@RestController
@RequestMapping(path = "/thumbnails", produces = MediaType.APPLICATION_JSON_VALUE)
public class ThumbnailRESTController {

    @Autowired
    private S3Service s3Svc;
    
    @GetMapping(path = "/{id}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String id) {
        return s3Svc.get(id);
    }
}
