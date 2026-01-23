package com.unifor.backend.repository

import com.unifor.backend.entity.Asset
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetRepository : JpaRepository<Asset, String> {
    fun findByAuthorId(authorId: String): List<Asset>
    fun findByIsPublicTrue(sort: Sort): List<Asset>
