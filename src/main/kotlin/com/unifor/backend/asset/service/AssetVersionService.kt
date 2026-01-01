package com.uniforge.backend.asset.service

import com.uniforge.backend.asset.entity.AssetVersion
import com.uniforge.backend.asset.repository.AssetRepository
import com.uniforge.backend.asset.repository.AssetVersionRepository
import org.springframework.stereotype.Service

@Service
class AssetVersionService(
    private val assetRepository: AssetRepository,
    private val versionRepository: AssetVersionRepository
) {
    fun createVersion(assetId: Long, s3Path: String): AssetVersion {
        val asset = assetRepository.findById(assetId).orElseThrow()
        return versionRepository.save(
            AssetVersion(
                asset = asset,
                s3RootPath = s3Path,
                status = "DRAFT"
            )
        )
    }

    fun publish(versionId: Long) {
        val version = versionRepository.findById(versionId).orElseThrow()
        versionRepository.save(
            AssetVersion(
                id = version.id,
                asset = version.asset,
                s3RootPath = version.s3RootPath,
                status = "PUBLISHED",
                createdAt = version.createdAt
            )
        )
    }
}
