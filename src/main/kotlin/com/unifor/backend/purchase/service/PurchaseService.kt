package com.unifor.backend.purchase.service

import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.repository.LibraryRepository
import com.unifor.backend.purchase.entity.Purchase
import com.unifor.backend.purchase.repository.PurchaseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val libraryRepository: LibraryRepository
) {

    @Transactional
    fun purchaseAsset(userId: String, assetVersionId: String) {
        purchaseRepository.save(
            Purchase(
                userId = userId,
                assetVersionId = assetVersionId
            )
        )

        libraryRepository.save(
            LibraryItem(
                userId = userId,
                itemType = "ASSET",
                refId = assetVersionId
            )
        )
    }
}



