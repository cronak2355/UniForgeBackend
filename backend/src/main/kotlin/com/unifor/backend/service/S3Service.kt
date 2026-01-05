package com.unifor.backend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket:uniforge-assets}") private val bucketName: String
) {
    
    /**
     * Generate a presigned URL for uploading a file to S3
     */
    fun generatePresignedUploadUrl(
        key: String,
        contentType: String,
        expirationMinutes: Long = 15
    ): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()
        
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .putObjectRequest(putObjectRequest)
            .build()
        
        val presignedRequest = s3Presigner.presignPutObject(presignRequest)
        return presignedRequest.url().toString()
    }
    
    /**
     * Get the public URL for an object in S3
     */
    fun getObjectUrl(key: String): String {
        return "https://$bucketName.s3.amazonaws.com/$key"
    }
}
