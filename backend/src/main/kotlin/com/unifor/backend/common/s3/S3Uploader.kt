package com.unifor.backend.common.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Component
class S3Uploader(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val bucket: String
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

    fun uploadFile(key: String, bytes: ByteArray, contentType: String): String {
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromBytes(bytes))
        return "s3://$bucket/$key"
    }

    fun getDownloadUrl(key: String, expirationMinutes: Long = 60): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .getObjectRequest(getObjectRequest)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    fun generatePresignedUploadUrl(key: String, contentType: String, expirationMinutes: Long = 15): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .putObjectRequest(putObjectRequest)
            .build()

        return s3Presigner.presignPutObject(presignRequest).url().toString()
    }
}




