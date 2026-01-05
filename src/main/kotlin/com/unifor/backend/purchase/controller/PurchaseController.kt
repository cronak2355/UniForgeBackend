package com.unifor.backend.purchase.controller

import com.unifor.backend.purchase.service.PurchaseService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/purchase")
class PurchaseController(
    private val purchaseService: PurchaseService
) {
    @PostMapping("/asset")
    fun purchaseAsset(
        @AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal,
        @RequestParam assetVersionId: String
    ) {
        purchaseService.purchaseAsset(user.id, assetVersionId)
    }
}



