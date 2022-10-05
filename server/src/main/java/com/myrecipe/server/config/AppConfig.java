package com.myrecipe.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    // @Value("${DO_SPACE_SECRET}")
    // private String secret;

    // @Value("${DO_SPACE_KEY}")
    // private String key;

    // @Value("${DO_SPACE_REGION}")
    // private String region = "sgp1";

    // @Value("${DO_SPACE_ENDPOINT}")
    // private String endpoint = "sgp1.digitaloceanspaces.com";

    // @Bean
    // public AmazonS3 createS3Client() {
    //     EndpointConfiguration config = new EndpointConfiguration(endpoint, region);
    //     BasicAWSCredentials cred = new BasicAWSCredentials(key, secret);
    //     return AmazonS3ClientBuilder.standard()
    //                                 .withEndpointConfiguration(config)
    //                                 .withCredentials(new AWSStaticCredentialsProvider(cred))
    //                                 .build();
    // }
}
