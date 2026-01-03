package com.uniforge.backend.purchase.controller

import com.uniforge.backend.purchase.service.PurchaseService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/purchase")
class PurchaseController(
    private val purchaseService: PurchaseService
) {
    @PostMapping("/asset")
    fun purchaseAsset(
        @RequestParam userId: Long,
        @RequestParam assetVersionId: Long
    ) {
        purchaseService.purchaseAsset(userId, assetVersionId)
    }
}
