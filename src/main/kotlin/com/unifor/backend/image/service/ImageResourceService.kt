package com.uniforge.backend.image.service

import com.uniforge.backend.image.dto.CreateImageResourceRequest
import com.uniforge.backend.image.entity.ImageResource
import com.uniforge.backend.image.repository.ImageResourceRepository
import org.springframework.stereotype.Service

@@Service
class ImageResourceService(
    private val imageResourceRepository: ImageResourceRepository,
    private val s3ObjectValidator: S3ObjectValidator,
    @Value("\${cloud.aws.s3.bucket}") private val bucketName: String
)
 {

    fun create(request: CreateImageResourceRequest): ImageResource {

        if (repository.existsByS3Key(request.s3Key)) {
            throw IllegalStateException("이미 등록된 이미지입니다.")
        }

        s3Validator.validateExists(
            bucket = bucketName,
            s3Key = request.s3Key,
            expectedContentTypePrefix = "image/"
        )
        return repository.save(
            ImageResource(
                ownerType = request.ownerType,
                ownerId = request.ownerId,
                imageType = request.imageType,
                s3Key = request.s3Key,
                contentType = request.contentType
            )
        )
    }
}
