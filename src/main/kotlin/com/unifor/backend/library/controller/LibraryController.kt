package com.uniforge.backend.library.controller

import com.uniforge.backend.library.service.LibraryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/library")
class LibraryController(
    private val libraryService: LibraryService
) {
    @GetMapping
    fun getLibrary(@RequestParam userId: Long) =
        libraryService.getUserLibrary(userId)
}
