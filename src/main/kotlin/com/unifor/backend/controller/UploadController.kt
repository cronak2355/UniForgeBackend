package com.unifor.backend.controller

import com.unifor.backend.upload.service.PresignService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/uploads")
class UploadController(
    private val presignService: PresignService
) {

    @PostMapping("/presign/image")
    fun getPresignedUrl(
        @RequestParam ownerType: String,
        @RequestParam ownerId: String,
        @RequestParam imageType: String,
        @RequestParam contentType: String
    ): Map<String, String> {
        // Generate S3 Key and Sign URL
        val presignResult = presignService.generateImageUploadUrl(
            ownerType = ownerType,
            ownerId = ownerId,
            imageType = imageType,
            contentType = contentType
        )
        
        // Return matching the frontend expectation
        return mapOf(
            "uploadUrl" to (presignResult["uploadUrl"] ?: ""),
            "s3Key" to (presignResult["s3Key"] ?: ""),
            "publicUrl" to (presignResult["publicUrl"] ?: "") // Optional, if needed strictly
        )
    }
}
