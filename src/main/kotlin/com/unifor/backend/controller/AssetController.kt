package com.unifor.backend.controller

import com.unifor.backend.entity.Asset
import com.unifor.backend.entity.AssetVersion
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.AssetVersionRepository
import com.unifor.backend.repository.UserRepository
import com.unifor.backend.security.UserPrincipal
import com.unifor.backend.service.S3Service
import com.unifor.backend.upload.service.PresignService
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
    private val libraryService: com.unifor.backend.library.service.LibraryService,
    private val presignService: PresignService
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
        @RequestParam(required = false) authorId: String?,
        @RequestParam(required = false, defaultValue = "latest") sort: String
    ): ResponseEntity<List<AssetResponse>> {
        val sortOption = when (sort) {
            "popular" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "rating") // Assuming rating exists, or fallback
            "price_asc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price")
            "price_desc" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "price")
            else -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt") // "latest"
        }

        val assets = if (authorId != null) {
            // Note: findByAuthorId needs to support Sort or we filter manually. 
            // For now, simpler to use existing and sort in memory if list is small, or strictly use separate Jpa methods.
            // Let's stick to memory sort for authorId for now to avoid repo changes, 
            // but for findAll we can pass Sort to JpaRepository's findAll(Sort).
            assetRepository.findByAuthorId(authorId).sortedWith(getComparator(sort))
        } else {
            assetRepository.findAll(sortOption)
        }
        return ResponseEntity.ok(assets.map { toResponse(it) })
    }

    private fun getComparator(sort: String): Comparator<Asset> {
        return when (sort) {
            "price_asc" -> compareBy { it.price }
            "price_desc" -> compareByDescending { it.price }
            "latest" -> compareByDescending { it.createdAt }
             else -> compareByDescending { it.createdAt }
        }
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

    @GetMapping("/{assetId}/versions/{versionId}/upload-url")
    fun getUploadUrl(
        @PathVariable assetId: String,
        @PathVariable versionId: String,
        @RequestParam contentType: String,
        @RequestParam(required = false, defaultValue = "preview") imageType: String
    ): Map<String, String> {
        val presignResult = presignService.generateImageUploadUrl(
            ownerType = "ASSET",
            ownerId = assetId,
            imageType = imageType,
            contentType = contentType
        )
        
        val uploadUrl = presignResult["uploadUrl"] ?: throw RuntimeException("Failed to generate upload URL")
        val s3Key = presignResult["s3Key"] ?: throw RuntimeException("Failed to generate S3 key")
        val readUrl = presignService.generatePresignedGetUrl(s3Key)
        
        return mapOf(
            "uploadUrl" to uploadUrl,
            "readUrl" to readUrl
        )
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
                genre = request.genre ?: "Other",
                imageUrl = request.imageUrl
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

    @PatchMapping("/{id}")
    fun updateAsset(
        @PathVariable id: String,
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UpdateAssetRequest
    ): ResponseEntity<AssetResponse> {
        val asset = assetRepository.findById(id).orElseThrow { RuntimeException("Asset not found") }
        
        if (asset.authorId != user.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        
        val updatedAsset = asset.copy(
            name = request.name ?: asset.name,
            price = request.price ?: asset.price,
            description = request.description ?: asset.description,
            isPublic = request.isPublic ?: asset.isPublic,
            genre = request.genre ?: asset.genre,
            imageUrl = request.imageUrl ?: asset.imageUrl
        )
        
        return ResponseEntity.ok(toResponse(assetRepository.save(updatedAsset)))
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
    val genre: String? = "Other",
    val imageUrl: String? = null
)

data class UpdateAssetRequest(
    val name: String? = null,
    val price: BigDecimal? = null,
    val description: String? = null,
    val isPublic: Boolean? = null,
    val genre: String? = null,
    val imageUrl: String? = null
)

data class CreateVersionRequest(
    val s3RootPath: String? = null
)

data class AssetResponse(
    val id: String,
    val name: String,
    val price: java.math.BigDecimal,
    val description: String?,
    val authorId: String,
    val authorName: String,
    val imageUrl: String?,
    val createdAt: java.time.Instant
)



