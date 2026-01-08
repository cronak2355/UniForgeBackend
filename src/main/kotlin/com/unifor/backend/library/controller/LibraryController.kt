package com.unifor.backend.library.controller

import com.unifor.backend.library.service.LibraryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/library")
class LibraryController(
    private val libraryService: LibraryService
) {
    @GetMapping
    fun getLibrary(@RequestParam userId: String) =
        libraryService.getUserLibrary(userId)

    @PostMapping
    fun addToLibrary(
        @org.springframework.security.core.annotation.AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal,
        @RequestBody request: AddToLibraryRequest
    ) = libraryService.addToLibrary(user.id, request.refId, request.itemType)

    // Collection Endpoints
    @GetMapping("/collections")
    fun getCollections(
        @org.springframework.security.core.annotation.AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal
    ) = libraryService.getUserCollections(user.id)

    @PostMapping("/collections")
    fun createCollection(
        @org.springframework.security.core.annotation.AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal,
        @RequestBody request: CreateCollectionRequest
    ) = libraryService.createCollection(user.id, request.name)

    @PutMapping("/items/{itemId}/move")
    fun moveItem(
        @org.springframework.security.core.annotation.AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal,
        @PathVariable itemId: String,
        @RequestBody request: MoveItemRequest
    ) = libraryService.moveItemToCollection(user.id, itemId, request.collectionId)
}

data class AddToLibraryRequest(
    val refId: String,
    val itemType: String
)

data class CreateCollectionRequest(
    val name: String
)

data class MoveItemRequest(
    val collectionId: String? // Nullable to remove from collection
)

