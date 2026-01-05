package com.uniforge.backend.purchase.service

import com.uniforge.backend.library.entity.LibraryItem
import com.uniforge.backend.library.repository.LibraryRepository
import com.uniforge.backend.purchase.entity.Purchase
import com.uniforge.backend.purchase.repository.PurchaseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val libraryRepository: LibraryRepository
) {

    @Transactional
    fun purchaseAsset(userId: Long, assetVersionId: Long) {
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
