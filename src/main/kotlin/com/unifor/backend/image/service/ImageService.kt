package com.unifor.backend.image.service

import com.unifor.backend.image.entity.ImageResource
import com.unifor.backend.image.repository.ImageResourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageService(
    private val imageResourceRepository: ImageResourceRepository
) {

    @Transactional
    fun registerImage(
        ownerType: String,
        ownerId: String,
        imageType: String,
        s3Key: String,
        contentType: String? = null
    ): ImageResource {
        
        // Disable old active images for this owner/type if necessary (optional logic)
        // val existing = imageResourceRepository.findByOwnerTypeAndOwnerIdAndImageTypeAndIsActive(
        //     ownerType, ownerId, imageType, true
        // )
        // existing.forEach { it.isActive = false }
        // imageResourceRepository.saveAll(existing)

        val image = ImageResource(
            ownerType = ownerType,
            ownerId = ownerId,
            imageType = imageType,
            s3Key = s3Key,
            isActive = true
        )
        return imageResourceRepository.save(image)
    }

    fun getImage(id: String): ImageResource {
        return imageResourceRepository.findById(id).orElseThrow { RuntimeException("Image not found") }
    }
}
