package com.unifor.backend.image.controller

import com.unifor.backend.image.service.ImageThumbnailService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/images")
class ImageThumbnailController(
    private val imageThumbnailService: ImageThumbnailService
) {

    @PostMapping("/thumbnail/replace")
    fun replaceThumbnail(
        @RequestParam ownerType: String,
        @RequestParam ownerId: String,
        @RequestParam imageId: String
    ) {
        imageThumbnailService.replaceThumbnail(
            ownerType = ownerType,
            ownerId = ownerId,
            newImageId = imageId
        )
    }
}



