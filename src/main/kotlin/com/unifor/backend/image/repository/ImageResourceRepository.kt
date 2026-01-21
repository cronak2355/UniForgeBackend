package com.unifor.backend.image.repository

import com.unifor.backend.image.entity.ImageResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageResourceRepository : JpaRepository<ImageResource, String> {
    fun findByOwnerTypeAndOwnerIdAndImageTypeAndIsActive(
        ownerType: String,
        ownerId: String,
        imageType: String,
        isActive: Boolean
    ): List<ImageResource>
}
