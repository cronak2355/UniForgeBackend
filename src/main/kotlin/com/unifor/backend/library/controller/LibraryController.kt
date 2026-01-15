package com.unifor.backend.library.controller

import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.service.LibraryService
import com.unifor.backend.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

data class AddToLibraryRequest(
    val refId: String,
    val itemType: String
)

data class LibraryResponse(
    val id: String,
    val userId: String,
    val refId: String?,
    val itemType: String?,
    val createdAt: LocalDateTime
)

@RestController
@RequestMapping("/library")
class LibraryController(
    private val libraryService: LibraryService
) {

    @GetMapping
    fun getLibrary(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<List<LibraryResponse>> {
        val items = libraryService.getLibrary(user.id)
        val response = items.map { item ->
            LibraryResponse(
                id = item.id,
                userId = item.userId,
                refId = item.targetId,
                itemType = item.targetType,
                createdAt = item.createdAt
            )
        }
        return ResponseEntity.ok(response)
    }

    // Placeholder for collections (not implemented in service yet, but prevents 404)
    @GetMapping("/collections")
    fun getCollections(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<List<Any>> {
        return ResponseEntity.ok(emptyList())
    }

    @PostMapping
    fun addToLibrary(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: AddToLibraryRequest
    ): ResponseEntity<Void> {
        libraryService.addToLibrary(user.id, request.refId, request.itemType)
        return ResponseEntity.ok().build()
    }
}
