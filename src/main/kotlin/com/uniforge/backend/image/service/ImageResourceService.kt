package com.uniforge.backend.image.service

import com.uniforge.backend.image.dto.CreateImageResourceRequest
import com.uniforge.backend.image.entity.ImageResource
import com.uniforge.backend.image.repository.ImageResourceRepository
import com.uniforge.backend.common.s3.S3ObjectValidator // Added this import based on the instruction description
import org.springframework.beans.factory.annotation.Value // Added this import for @Value
import org.springframework.stereotype.Service

@Service
class ImageResourceService(
    private val imageResourceRepository: ImageResourceRepository,
    private val s3ObjectValidator: S3ObjectValidator,
    @Value("\${cloud.aws.s3.bucket}") private val bucketName: String
)
 {

    fun create(request: CreateImageResourceRequest): ImageResource {

        if (imageResourceRepository.existsByS3Key(request.s3Key)) { // Changed 'repository' to 'imageResourceRepository'
            throw IllegalStateException("이미 등록된 이미지입니다.")
        }

        s3ObjectValidator.validateExists(
            bucket = bucketName,
            s3Key = request.s3Key,
            expectedContentTypePrefix = "image/"
        )
        return imageResourceRepository.save(
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
