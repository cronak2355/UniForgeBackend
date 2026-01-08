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
}

data class AddToLibraryRequest(
    val refId: String,
    val itemType: String
)

