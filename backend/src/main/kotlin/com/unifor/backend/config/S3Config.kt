package com.unifor.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config(
    @Value("\${aws.s3.access-key:}") private val accessKey: String,
    @Value("\${aws.s3.secret-key:}") private val secretKey: String,
    @Value("\${aws.s3.region:ap-northeast-2}") private val region: String
) {
    
    @Bean
    fun s3Client(): S3Client {
        return if (accessKey.isNotBlank() && secretKey.isNotBlank()) {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
        } else {
            // Use default credentials chain (EC2 instance profile, etc.)
            S3Client.builder()
                .region(Region.of(region))
                .build()
        }
    }
    
    @Bean
    fun s3Presigner(): S3Presigner {
        return if (accessKey.isNotBlank() && secretKey.isNotBlank()) {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
        } else {
            S3Presigner.builder()
                .region(Region.of(region))
                .build()
        }
    }
}
