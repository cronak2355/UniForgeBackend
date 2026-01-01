package com.uniforge.backend.common.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3Uploader(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {
    fun uploadJson(key: String, json: String): String {
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType("application/json")
            .build()

        s3Client.putObject(request, RequestBody.fromString(json))
        return "s3://$bucket/$key"
    }
}
