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
    fun generateImageUploadUrl(
        ownerType: String,   // GAME | ASSET
        ownerId: String,
        imageType: String,   // thumbnail | preview
        contentType: String
    ): Map<String, String> {

        if (!contentType.startsWith("image/") && !contentType.startsWith("audio/")) {
            throw IllegalArgumentException("Only image or audio uploads are allowed")
        }

        val extension = when (contentType) {
            "image/png" -> "png"
            "image/jpeg" -> "jpg"
            "image/webp" -> "webp"
            "audio/mpeg" -> "mp3"
            "audio/wav" -> "wav"
            "audio/ogg" -> "ogg"
            "audio/x-m4a" -> "m4a"
            else -> throw IllegalArgumentException("Unsupported file type")
        }

        val folder = if (contentType.startsWith("audio/")) "sounds" else "images"
        val key = buildKey(ownerType, ownerId, imageType, extension, folder)

        val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(putRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest)

        // S3 직접 URL로 이미지 접근 (CloudFront 라우팅 문제 우회)
        // If region is needed, it must be injected or hardcoded if logic fails. 
        // Assuming region is available or we construct string. 
        // The original code used 'region' var but constructor in main might not have it.
        // Let's check constructor! 
        val publicUrl = "https://$bucket.s3.ap-northeast-2.amazonaws.com/$key"

        return mapOf(
            "uploadUrl" to presignedUrl.url().toString(),
            "s3Key" to key,
            "publicUrl" to publicUrl
        )
    }

    private fun buildKey(
        ownerType: String,
        ownerId: String,
        imageType: String,
        extension: String,
        folder: String = "images" 
    ): String {
        val uuid = UUID.randomUUID().toString()

        return when (ownerType) {
            "GAME" ->
                "games/$ownerId/$folder/$imageType-$uuid.$extension"
            "ASSET" ->
                "assets/$ownerId/$folder/$imageType-$uuid.$extension"
            else ->
                throw IllegalArgumentException("Invalid ownerType")
        }
    }

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
}
