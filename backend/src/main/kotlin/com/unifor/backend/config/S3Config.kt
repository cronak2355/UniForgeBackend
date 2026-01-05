package com.unifor.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config {

    private fun credentialsProvider() = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(
            System.getenv("AWS_ACCESS_KEY") ?: System.getenv("AWS_ACCESS_KEY_ID"),
            System.getenv("AWS_SECRET_KEY") ?: System.getenv("AWS_SECRET_ACCESS_KEY")
        )
    )

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(credentialsProvider())
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(credentialsProvider())
            .build()
    }
}


