package com.unifor.backend.controller

import com.unifor.backend.entity.Asset
import com.unifor.backend.entity.AssetVersion
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.AssetVersionRepository
import com.unifor.backend.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
    private val libraryService: com.unifor.backend.library.service.LibraryService
) {
    
    private fun toResponse(asset: Asset): AssetResponse {
        val authorName = userRepository.findById(asset.authorId).map { it.name }.orElse("Unknown")
        return AssetResponse(
            id = asset.id,
            name = asset.name,
            price = asset.price,
            description = asset.description,
            authorId = asset.authorId,
            authorName = authorName,
            imageUrl = asset.imageUrl,
            createdAt = asset.createdAt
        )
    }
    
    // ============ 공개 엔드포인트 (인증 불필요) ============
    
    @GetMapping
    fun getAssets(
        @RequestParam(required = false) authorId: String?
    ): ResponseEntity<List<AssetResponse>> {
        val assets = if (authorId != null) {
            assetRepository.findByAuthorId(authorId)
        } else {
            assetRepository.findAll()
        }
        return ResponseEntity.ok(assets.map { toResponse(it) })
    }
    
    @GetMapping("/{id}")
    fun getAsset(@PathVariable id: String): ResponseEntity<AssetResponse> {
        return assetRepository.findById(id)
            .map { ResponseEntity.ok(toResponse(it)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @GetMapping("/{assetId}/versions")
    fun getVersions(@PathVariable assetId: String): ResponseEntity<List<AssetVersion>> {
        val versions = assetVersionRepository.findByAssetId(assetId)
        return ResponseEntity.ok(versions)
    }
    
    // ============ 인증 필요 엔드포인트 ============
    
    @PostMapping
    fun createAsset(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: CreateAssetRequest
    ): ResponseEntity<AssetResponse> {
        val asset = assetRepository.save(
            Asset(
                name = request.name,
                price = request.price ?: BigDecimal.ZERO,
                description = request.description,
                authorId = user.id,
                isPublic = request.isPublic ?: true,
                genre = request.genre ?: "Other"
            )
        )
        
        // Auto-add to author's library
        try {
            libraryService.addToLibrary(user.id, asset.id, "ASSET")
        } catch (e: Exception) {
            // Ignore if fails, not critical
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(asset))
    }
    
    // ... existing createVersion ...

    // ... existing endpoints ...
}

// ... existing AssetResponse ...

// Request DTOs
data class CreateAssetRequest(
    val name: String,
    val price: BigDecimal? = null,
    val description: String? = null,
    val isPublic: Boolean? = true,
    val genre: String? = "Other"
)

data class CreateVersionRequest(
    val s3RootPath: String? = null
)



