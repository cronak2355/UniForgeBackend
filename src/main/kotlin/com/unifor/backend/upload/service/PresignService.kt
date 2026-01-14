package com.unifor.backend.upload.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class PresignService(
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val bucket: String
) {
    
    fun generatePresignedGetUrl(key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()
            
        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build()
            
        return s3Presigner.presignGetObject(presignRequest).url().toExternalForm()
    }

    fun generateImageUploadUrl(
        ownerType: String,
        ownerId: String,
        imageType: String,
        contentType: String
    ): Map<String, String> {
        val s3Key = "uploads/$ownerType/$ownerId/$imageType/${UUID.randomUUID()}"
        
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .contentType(contentType)
            .build()
            
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(putObjectRequest)
            .build()
            
        val uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toExternalForm()
        
        return mapOf(
            "uploadUrl" to uploadUrl,
            "s3Key" to s3Key
        )
    }
}
