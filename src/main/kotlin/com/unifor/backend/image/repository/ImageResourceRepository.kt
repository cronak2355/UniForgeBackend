package com.unifor.backend.image.repository

import com.unifor.backend.image.entity.ImageResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying // Added
import org.springframework.data.jpa.repository.Query // Added

interface ImageResourceRepository : JpaRepository<ImageResource, String> {

    fun existsByS3Key(s3Key: String): Boolean

    fun findByOwnerTypeAndOwnerIdAndImageTypeAndIsActive(
        ownerType: String,
        ownerId: String,
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
        ownerId: String,
        imageType: String
    )
}



