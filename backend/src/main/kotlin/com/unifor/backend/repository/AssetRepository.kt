package com.unifor.backend.repository

import com.unifor.backend.entity.Asset
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetRepository : JpaRepository<Asset, String> {
    fun findByAuthorId(authorId: String): List<Asset>
    fun findAllByIsPublicTrue(): List<Asset>
    fun findByAuthorIdAndIsPublicTrue(authorId: String): List<Asset>
}



