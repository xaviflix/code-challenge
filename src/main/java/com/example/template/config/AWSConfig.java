package com.example.template.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AWSConfig {

    private static final Logger log = LoggerFactory.getLogger(AWSConfig.class);

    @Autowired
    private Environment env;

    public AWSCredentials credentials() {
        AWSCredentials credentials = new BasicAWSCredentials(
                "fakeaccesskey",
                "fakesecretkey"
        );
        return credentials;
    }

    @Bean
    public AmazonSQS amazonSQS() {

        log.info("SQS endpoint configuration {}", env.getProperty("sqs.endpoint.configuration"));

        return AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(getEndpointConfiguration(env.getProperty("sqs.endpoint.configuration")))
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .build();
    }

    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(String url) {
        return new AwsClientBuilder.EndpointConfiguration(url, Regions.US_WEST_1.getName());
    }
}
