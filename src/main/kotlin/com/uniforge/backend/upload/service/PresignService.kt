package com.uniforge.backend.upload.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.*

@Service
class PresignService(
    private val presigner: S3Presigner,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {

    fun generateImageUploadUrl(
        ownerType: String,   // GAME | ASSET
        ownerId: Long,
        imageType: String,   // thumbnail | preview
        contentType: String
    ): Map<String, String> {

        if (!contentType.startsWith("image/")) {
            throw IllegalArgumentException("Only image uploads are allowed")
        }

        val extension = when (contentType) {
            "image/png" -> "png"
            "image/jpeg" -> "jpg"
            "image/webp" -> "webp"
            else -> throw IllegalArgumentException("Unsupported image type")
        }

        val key = buildKey(ownerType, ownerId, imageType, extension)

        val putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(putRequest)
            .build()

        val presignedUrl = presigner.presignPutObject(presignRequest)

        return mapOf(
            "uploadUrl" to presignedUrl.url().toString(),
            "s3Key" to key
        )
    }

    private fun buildKey(
        ownerType: String,
        ownerId: Long,
        imageType: String,
        extension: String
    ): String {
        val uuid = UUID.randomUUID().toString()

        return when (ownerType) {
            "GAME" ->
                "games/$ownerId/images/$imageType-$uuid.$extension"
            "ASSET" ->
                "assets/$ownerId/images/$imageType-$uuid.$extension"
            else ->
                throw IllegalArgumentException("Invalid ownerType")
        }
    }
}
