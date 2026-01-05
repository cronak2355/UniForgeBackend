package com.uniforge.backend.image.controller

import com.uniforge.backend.image.service.ImageThumbnailService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/images")
class ImageThumbnailController(
    private val imageThumbnailService: ImageThumbnailService
) {

    @PostMapping("/thumbnail/replace")
    fun replaceThumbnail(
        @RequestParam ownerType: String,
        @RequestParam ownerId: Long,
        @RequestParam imageId: Long
    ) {
        imageThumbnailService.replaceThumbnail(
            ownerType = ownerType,
            ownerId = ownerId,
            newImageId = imageId
        )
    }
}
