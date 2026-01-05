package com.unifor.backend.image.service

import com.unifor.backend.image.entity.ImageResource
import com.unifor.backend.image.repository.ImageResourceRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ImageThumbnailService(
    private val imageResourceRepository: ImageResourceRepository
) {

    @Transactional
    fun replaceThumbnail(
        ownerType: String,
        ownerId: String,
        newImageId: String
    ) {
        val newImage = imageResourceRepository.findById(newImageId)
            .orElseThrow { IllegalArgumentException("Image not found") }

        if (
            newImage.ownerType != ownerType ||
            newImage.ownerId != ownerId ||
            newImage.imageType != "THUMBNAIL"
        ) {
            throw IllegalStateException("Invalid thumbnail image")
        }

        imageResourceRepository.deactivateCurrentThumbnail(
            ownerType = ownerType,
            ownerId = ownerId,
            imageType = "THUMBNAIL"
        )

        val activated = ImageResource(
            id = newImage.id,
            ownerType = newImage.ownerType,
            ownerId = newImage.ownerId,
            imageType = newImage.imageType,
            s3Key = newImage.s3Key,
            contentType = newImage.contentType,
            isActive = true,
            createdAt = newImage.createdAt
        )

        imageResourceRepository.save(activated)
    }
}



