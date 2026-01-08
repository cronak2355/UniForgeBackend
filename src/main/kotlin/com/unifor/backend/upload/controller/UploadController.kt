package com.unifor.backend.upload.controller

import com.unifor.backend.upload.service.PresignService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uploads")
class UploadController(
    private val presignService: PresignService
) {

    @PostMapping("/presign/image")
    fun presignImageUpload(
        @RequestParam ownerType: String,
        @RequestParam ownerId: String,
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



