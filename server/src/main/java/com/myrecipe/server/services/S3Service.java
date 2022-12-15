package com.myrecipe.server.services;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Service
public class S3Service {

    // Use local db instead
    // @Autowired
    // private AmazonS3 s3;

    // public Optional<String> upload(final MultipartFile file, String uploaderName) {
    //     try{
    //         String objId = UUID.randomUUID().toString().substring(0, 8);
    //         ObjectMetadata metadata = new ObjectMetadata();
    //         metadata.setContentType(file.getContentType());
    //         metadata.setContentLength(file.getSize());
    //         metadata.addUserMetadata("uploader", uploaderName);

    //         PutObjectRequest putReq = new PutObjectRequest("myrecipe", "thumbnails/%s".formatted(objId), file.getInputStream(), metadata);
    //         putReq.setCannedAcl(CannedAccessControlList.PublicRead);
    //         s3.putObject(putReq);

    //         return Optional.of(objId);

    //     } catch (IOException ex) {
    //         ex.printStackTrace();
    //         return Optional.empty();
    //     }
    // }

    // public ResponseEntity<byte[]> get(String objId) {
    //     try{
    //         GetObjectRequest getReq = new GetObjectRequest("myrecipe", "thumbnails/%s".formatted(objId));
    //         S3Object result = s3.getObject(getReq);
    //         ObjectMetadata metadata = result.getObjectMetadata();
    //         Map<String, String> userData = metadata.getUserMetadata();
    //         try (S3ObjectInputStream is = result.getObjectContent()){
    //             byte[] buffer = is.readAllBytes();
    //             return ResponseEntity.status(HttpStatus.OK)
    //                                 .contentLength(metadata.getContentLength())
    //                                 .contentType(MediaType.parseMediaType(metadata.getContentType()))
    //                                 .header("X-Uploader", userData.get("uploader"))
    //                                 .body(buffer);
    //         }
    //     } catch(Exception e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    // public void delete(final String key) {
    //     DeleteObjectRequest deleteReq = new DeleteObjectRequest("myrecipe", "thumbnails/%s".formatted(key));
    //     s3.deleteObject(deleteReq);
    // }
    
}
