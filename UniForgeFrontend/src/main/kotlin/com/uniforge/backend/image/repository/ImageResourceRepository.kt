package com.uniforge.backend.image.repository

import com.uniforge.backend.image.entity.ImageResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying // Added
import org.springframework.data.jpa.repository.Query // Added

interface ImageResourceRepository : JpaRepository<ImageResource, Long> {

    fun existsByS3Key(s3Key: String): Boolean

    fun findByOwnerTypeAndOwnerIdAndImageTypeAndIsActive(
        ownerType: String,
        ownerId: Long,
        imageType: String,
        isActive: Boolean
    ): ImageResource?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ImageResource ir
        set ir.isActive = false
        where ir.ownerType = :ownerType
          and ir.ownerId = :ownerId
          and ir.imageType = :imageType
          and ir.isActive = true
    """)
    fun deactivateCurrentThumbnail(
        ownerType: String,
        ownerId: Long,
        imageType: String
    )
}
