package com.unifor.backend.common.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.model.GetUrlRequest

@Service
class S3Uploader(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val bucket: String
) {
    
    fun getDownloadUrl(s3Key: String): String {
        val getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .build()
            
        val presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
            .signatureDuration(java.time.Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build()
            
        return s3Presigner.presignGetObject(presignRequest).url().toExternalForm()
    }
}
