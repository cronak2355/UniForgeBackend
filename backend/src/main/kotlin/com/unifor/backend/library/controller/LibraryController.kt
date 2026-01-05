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
}

