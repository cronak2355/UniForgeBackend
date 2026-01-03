package com.uniforge.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3PresignConfig {

    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        System.getenv("AWS_ACCESS_KEY_ID"),
                        System.getenv("AWS_SECRET_ACCESS_KEY")
                    )
                )
            )
            .build()
    }
}
