package com.unifor.backend.library.controller

import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.service.LibraryService
import com.unifor.backend.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

data class AddToLibraryRequest(
    val targetId: String,
    val targetType: String
)

@RestController
@RequestMapping("/library")
class LibraryController(
    private val libraryService: LibraryService
) {

    @GetMapping
    fun getLibrary(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<List<LibraryItem>> {
        val items = libraryService.getLibrary(user.id)
        return ResponseEntity.ok(items)
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
        libraryService.addToLibrary(user.id, request.targetId, request.targetType)
        return ResponseEntity.ok().build()
    }
}
