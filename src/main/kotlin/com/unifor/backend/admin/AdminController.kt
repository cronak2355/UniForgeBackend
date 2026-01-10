package com.unifor.backend.admin

import com.unifor.backend.entity.Asset
import com.unifor.backend.entity.User
import com.unifor.backend.entity.UserRole
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.AssetVersionRepository
import com.unifor.backend.repository.GameRepository
import com.unifor.backend.repository.UserRepository
import com.unifor.backend.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val userRepository: UserRepository,
    private val assetRepository: AssetRepository,
    private val assetVersionRepository: AssetVersionRepository,
    private val gameRepository: GameRepository
) {
    
    // ============ 시스템 통계 ============
    
    @GetMapping("/stats")
    fun getStats(): ResponseEntity<AdminStatsResponse> {
        val totalUsers = userRepository.count()
        val totalAssets = assetRepository.count()
        val totalGames = gameRepository.count()
        val adminCount = userRepository.findAll().count { it.role == UserRole.ADMIN }
        
        return ResponseEntity.ok(AdminStatsResponse(
            totalUsers = totalUsers,
            totalAssets = totalAssets,
            totalGames = totalGames,
            adminCount = adminCount.toLong()
        ))
    }
    
    // ============ 사용자 관리 ============
    
    @GetMapping("/users")
    fun getAllUsers(
        @RequestParam(required = false) role: UserRole?,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<List<AdminUserResponse>> {
        var users = userRepository.findAll()
        
        if (role != null) {
            users = users.filter { it.role == role }
        }
        
        if (!search.isNullOrBlank()) {
            users = users.filter { 
                it.email.contains(search, ignoreCase = true) || 
                it.name.contains(search, ignoreCase = true) 
            }
        }
        
        return ResponseEntity.ok(users.map { toUserResponse(it) })
    }
    
    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: String): ResponseEntity<AdminUserResponse> {
        return userRepository.findById(userId)
            .map { ResponseEntity.ok(toUserResponse(it)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @PatchMapping("/users/{userId}/role")
    fun updateUserRole(
        @PathVariable userId: String,
        @AuthenticationPrincipal admin: UserPrincipal,
        @RequestBody request: UpdateRoleRequest
    ): ResponseEntity<AdminUserResponse> {
        // 자기 자신의 권한은 변경 불가
        if (userId == admin.id) {
            return ResponseEntity.badRequest().build()
        }
        
        val user = userRepository.findById(userId).orElseThrow { 
            RuntimeException("User not found: $userId") 
        }
        
        user.role = request.role
        val savedUser = userRepository.save(user)
        
        return ResponseEntity.ok(toUserResponse(savedUser))
    }
    
    // ============ 에셋 관리 ============
    
    @GetMapping("/assets")
    fun getAllAssets(
        @RequestParam(required = false) authorId: String?,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<List<AdminAssetResponse>> {
        var assets = assetRepository.findAll()
        
        if (authorId != null) {
            assets = assets.filter { it.authorId == authorId }
        }
        
        if (!search.isNullOrBlank()) {
            assets = assets.filter { it.name.contains(search, ignoreCase = true) }
        }
        
        return ResponseEntity.ok(assets.map { toAssetResponse(it) })
    }
    
    @DeleteMapping("/assets/{assetId}")
    fun deleteAsset(
        @PathVariable assetId: String,
        @AuthenticationPrincipal admin: UserPrincipal
    ): ResponseEntity<DeleteResponse> {
        val asset = assetRepository.findById(assetId).orElseThrow {
            RuntimeException("Asset not found: $assetId")
        }
        
        // 관련 버전들 삭제
        val versions = assetVersionRepository.findByAssetId(assetId)
        assetVersionRepository.deleteAll(versions)
        
        // 에셋 삭제
        assetRepository.delete(asset)
        
        return ResponseEntity.ok(DeleteResponse(
            success = true,
            message = "Asset '${asset.name}' deleted by admin ${admin.email}",
            deletedId = assetId
        ))
    }
    
    // ============ Helper Methods ============
    
    private fun toUserResponse(user: User): AdminUserResponse {
        val assetCount = assetRepository.findByAuthorId(user.id).size
        val gameCount = gameRepository.findByAuthor_Id(user.id).size
        
        return AdminUserResponse(
            id = user.id,
            email = user.email,
            name = user.name,
            role = user.role,
            provider = user.provider.name,
            profileImage = user.profileImage,
            createdAt = user.createdAt,
            assetCount = assetCount,
            gameCount = gameCount
        )
    }
    
    private fun toAssetResponse(asset: Asset): AdminAssetResponse {
        val authorName = userRepository.findById(asset.authorId)
            .map { it.name }
            .orElse("Unknown")
        
        return AdminAssetResponse(
            id = asset.id,
            name = asset.name,
            description = asset.description,
            price = asset.price,
            authorId = asset.authorId,
            authorName = authorName,
            imageUrl = asset.imageUrl,
            isPublic = asset.isPublic,
            genre = asset.genre,
            createdAt = asset.createdAt
        )
    }
}

// ============ DTOs ============

data class AdminStatsResponse(
    val totalUsers: Long,
    val totalAssets: Long,
    val totalGames: Long,
    val adminCount: Long
)

data class AdminUserResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val provider: String,
    val profileImage: String?,
    val createdAt: Instant,
    val assetCount: Int,
    val gameCount: Int
)

data class AdminAssetResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: java.math.BigDecimal,
    val authorId: String,
    val authorName: String,
    val imageUrl: String?,
    val isPublic: Boolean,
    val genre: String?,
    val createdAt: Instant
)

data class UpdateRoleRequest(
    val role: UserRole
)

data class DeleteResponse(
    val success: Boolean,
    val message: String,
    val deletedId: String
)
