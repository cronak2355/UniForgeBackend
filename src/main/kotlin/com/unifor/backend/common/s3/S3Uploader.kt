package com.unifor.backend.common.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetUrlRequest

@Service
class S3Uploader(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket}") private val bucket: String
) {
    
    fun getDownloadUrl(s3Key: String): String {
        val request = GetUrlRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .build()
            
        return s3Client.utilities().getUrl(request).toExternalForm()
    }
}
