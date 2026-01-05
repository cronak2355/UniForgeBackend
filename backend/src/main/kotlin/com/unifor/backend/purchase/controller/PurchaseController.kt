package com.unifor.backend.purchase.controller

import com.unifor.backend.purchase.service.PurchaseService
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
