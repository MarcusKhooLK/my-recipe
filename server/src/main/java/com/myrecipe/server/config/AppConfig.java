package com.myrecipe.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AppConfig {
    
    @Value("${do.space.secret}")
    private String secret;

    @Value("${do.space.key}")
    private String key;

    @Value("${do.space.region}")
    private String region = "sgp1";

    @Value("${do.space.endpoint}")
    private String endpoint = "sgp1.digitaloceanspaces.com";

    @Bean
    public AmazonS3 createS3Client() {
        EndpointConfiguration config = new EndpointConfiguration(endpoint, region);
        BasicAWSCredentials cred = new BasicAWSCredentials(key, secret);
        return AmazonS3ClientBuilder.standard()
                                    .withEndpointConfiguration(config)
                                    .withCredentials(new AWSStaticCredentialsProvider(cred))
                                    .build();
    }
}
