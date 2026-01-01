package com.uniforge.backend.asset.controller

import com.uniforge.backend.asset.service.AssetService
import com.uniforge.backend.asset.service.AssetVersionService
import com.uniforge.backend.common.s3.S3Uploader
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/assets")
class AssetController(
    private val assetService: AssetService,
    private val assetVersionService: AssetVersionService,
    private val s3Uploader: S3Uploader
) {

    @PostMapping
    fun createAsset(
        @RequestParam authorId: Long,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?,
        @RequestParam price: Int
    ) = assetService.createAsset(authorId, name, description, price)

    @PostMapping("/{assetId}/versions")
    fun createVersion(
        @PathVariable assetId: Long,
        @RequestBody assetJson: String
    ): Any {
        val s3Path = s3Uploader.uploadJson(
            key = "assets/$assetId/${System.currentTimeMillis()}/asset.json",
            json = assetJson
        )

        return assetVersionService.createVersion(assetId, s3Path)
    }

    @PostMapping("/versions/{versionId}/publish")
    fun publish(@PathVariable versionId: Long) {
        assetVersionService.publish(versionId)
    }
}
