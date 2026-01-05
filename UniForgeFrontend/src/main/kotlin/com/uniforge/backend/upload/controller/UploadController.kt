package com.uniforge.backend.upload.controller

import com.uniforge.backend.upload.service.PresignService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uploads")
class UploadController(
    private val presignService: PresignService
) {

    @PostMapping("/presign/image")
    fun presignImageUpload(
        @RequestParam ownerType: String,
        @RequestParam ownerId: Long,
        @RequestParam imageType: String,
        @RequestParam contentType: String
    ): Map<String, String> {
        return presignService.generateImageUploadUrl(
            ownerType = ownerType,
            ownerId = ownerId,
            imageType = imageType,
            contentType = contentType
        )
    }
}
