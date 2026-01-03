package com.uniforge.backend.image.service

import com.uniforge.backend.image.entity.ImageResource
import com.uniforge.backend.image.repository.ImageResourceRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ImageThumbnailService(
    private val imageResourceRepository: ImageResourceRepository
) {

    @Transactional
    fun replaceThumbnail(
        ownerType: String,
        ownerId: Long,
        newImageId: Long
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
