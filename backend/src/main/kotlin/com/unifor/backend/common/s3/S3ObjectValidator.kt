package com.unifor.backend.common.s3

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import org.springframework.stereotype.Component

@Component
class S3ObjectValidator(
    private val s3Client: S3Client
) {

    fun validateExists(
        bucket: String,
        s3Key: String,
        expectedContentTypePrefix: String? = null
    ) {
        val response = try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build()
            )
        } catch (e: Exception) {
            throw IllegalStateException("S3 객체가 존재하지 않습니다: $s3Key")
        }

        if (response.contentLength() <= 0) {
            throw IllegalStateException("S3 객체의 크기가 0 byte 입니다.")
        }

        if (
            expectedContentTypePrefix != null &&
            response.contentType()?.startsWith(expectedContentTypePrefix) != true
        ) {
            throw IllegalStateException("허용되지 않은 Content-Type 입니다.")
        }
    }
}



