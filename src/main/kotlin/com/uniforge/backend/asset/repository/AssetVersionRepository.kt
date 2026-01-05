package com.uniforge.backend.asset.repository

import com.uniforge.backend.asset.entity.AssetVersion
import org.springframework.data.jpa.repository.JpaRepository

interface AssetVersionRepository : JpaRepository<AssetVersion, Long> {
    fun findTopByAssetIdAndStatusOrderByCreatedAtDesc(
        assetId: Long,
        status: String
    ): AssetVersion?

    fun findByAssetIdOrderByCreatedAtDesc(assetId: Long): List<AssetVersion>
}
