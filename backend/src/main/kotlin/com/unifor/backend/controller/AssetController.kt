package com.unifor.backend.controller

import com.unifor.backend.entity.Asset
import com.unifor.backend.entity.AssetVersion
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.AssetVersionRepository
import com.unifor.backend.security.UserPrincipal
import com.unifor.backend.service.S3Service
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/assets")
class AssetController(
    private val assetRepository: AssetRepository,
    private val assetVersionRepository: AssetVersionRepository,
    private val s3Service: S3Service
) {
    
    // ============ Í≥µÍ∞ú ?îÎìú?¨Ïù∏??(?∏Ï¶ù Î∂àÌïÑ?? ============
    
    @GetMapping
    fun getAssets(
        @RequestParam(required = false) authorId: String?
    ): ResponseEntity<List<Asset>> {
        val assets = if (authorId != null) {
            assetRepository.findByAuthorId(authorId)
        } else {
            assetRepository.findAll()
        }
        return ResponseEntity.ok(assets)
    }
    
    @GetMapping("/{id}")
    fun getAsset(@PathVariable id: String): ResponseEntity<Asset> {
        return assetRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @GetMapping("/{assetId}/versions")
    fun getVersions(@PathVariable assetId: String): ResponseEntity<List<AssetVersion>> {
        val versions = assetVersionRepository.findByAssetId(assetId)
        return ResponseEntity.ok(versions)
    }
    
    // ============ ?∏Ï¶ù ?ÑÏöî ?îÎìú?¨Ïù∏??============
    
    @PostMapping
    fun createAsset(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: CreateAssetRequest
    ): ResponseEntity<Asset> {
        val asset = assetRepository.save(
            Asset(
                name = request.name,
                price = request.price ?: BigDecimal.ZERO,
                description = request.description,
                authorId = user.id
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(asset)
    }
    
    @PostMapping("/{assetId}/versions")
    fun createVersion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable assetId: String,
        @RequestBody request: CreateVersionRequest
    ): ResponseEntity<AssetVersion> {
        // Check asset exists
        if (!assetRepository.existsById(assetId)) {
            return ResponseEntity.notFound().build()
        }
        
        val version = assetVersionRepository.save(
            AssetVersion(
                assetId = assetId,
                s3RootPath = request.s3RootPath
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(version)
    }
    
    @GetMapping("/{assetId}/versions/{versionId}/upload-url")
    fun getUploadUrl(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable assetId: String,
        @PathVariable versionId: String,
        @RequestParam fileName: String,
        @RequestParam contentType: String
    ): ResponseEntity<Map<String, String>> {
        // Generate presigned URL for S3 upload
        val s3Key = "assets/$assetId/versions/$versionId/$fileName"
        val uploadUrl = s3Service.generatePresignedUploadUrl(s3Key, contentType)
        return ResponseEntity.ok(mapOf(
            "uploadUrl" to uploadUrl,
            "s3Key" to s3Key
        ))
    }
    
    @PostMapping("/versions/{versionId}/publish")
    fun publishVersion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable versionId: String
    ): ResponseEntity<AssetVersion> {
        val version = assetVersionRepository.findById(versionId).orElse(null)
            ?: return ResponseEntity.notFound().build()
        
        version.status = "PUBLISHED"
        val updatedVersion = assetVersionRepository.save(version)
        return ResponseEntity.ok(updatedVersion)
    }
}

// Request DTOs
data class CreateAssetRequest(
    val name: String,
    val price: BigDecimal? = null,
    val description: String? = null
)

data class CreateVersionRequest(
    val s3RootPath: String? = null
)
