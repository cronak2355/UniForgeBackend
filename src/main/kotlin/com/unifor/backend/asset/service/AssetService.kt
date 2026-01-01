package com.uniforge.backend.asset.service

import com.uniforge.backend.asset.entity.Asset
import com.uniforge.backend.asset.repository.AssetRepository
import org.springframework.stereotype.Service

@Service
class AssetService(
    private val assetRepository: AssetRepository
) {
    fun createAsset(
        authorId: Long,
        name: String,
        description: String?,
        price: Int
    ): Asset {
        return assetRepository.save(
            Asset(
                authorId = authorId,
                name = name,
                description = description,
                price = price
            )
        )
    }
}
