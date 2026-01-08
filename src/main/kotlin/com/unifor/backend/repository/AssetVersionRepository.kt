package com.unifor.backend.repository

import com.unifor.backend.entity.AssetVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetVersionRepository : JpaRepository<AssetVersion, String> {
    fun findByAssetId(assetId: String): List<AssetVersion>
}



