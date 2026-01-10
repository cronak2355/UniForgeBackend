package com.unifor.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config(
    @org.springframework.beans.factory.annotation.Value("\${aws.s3.access-key}") private val accessKey: String,
    @org.springframework.beans.factory.annotation.Value("\${aws.s3.secret-key}") private val secretKey: String,
    @org.springframework.beans.factory.annotation.Value("\${aws.s3.region}") private val regionStr: String
) {

    private fun credentialsProvider(): StaticCredentialsProvider {
        // Fallback for tests or missing envs to prevent startup crash if property is empty,
        // although in Prod it should crash if missing.
        // But AwsBasicCredentials throws if null.
        // application.yml defaults to empty string if env is missing.
        // Check if empty and provide dummy if needed?
        // No, let's just pass it. If it fails in Prod due to missing key, that is correct.
        // In Test, application-test.yml provides "test-access-key".
        
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(regionStr))
            .credentialsProvider(credentialsProvider())
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.of(regionStr))
            .credentialsProvider(credentialsProvider())
            .build()
    }
}


