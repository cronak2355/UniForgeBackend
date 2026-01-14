package com.unifor.backend.image.controller

import com.unifor.backend.image.entity.ImageResource
import com.unifor.backend.image.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class CreateImageRequest(
    val ownerType: String,
    val ownerId: String,
    val imageType: String,
    val s3Key: String,
    val contentType: String
)

@RestController
@RequestMapping("/images")
class ImageController(
    private val imageService: ImageService
) {

    @PostMapping
    fun registerImage(@RequestBody request: CreateImageRequest): ResponseEntity<ImageResource> {
        val image = imageService.registerImage(
            ownerType = request.ownerType,
            ownerId = request.ownerId,
            imageType = request.imageType,
            s3Key = request.s3Key,
            contentType = request.contentType
        )
        return ResponseEntity.ok(image)
    }

    @GetMapping("/{id}")
    fun getImage(@PathVariable id: String): ResponseEntity<ImageResource> {
        val image = imageService.getImage(id)
        return ResponseEntity.ok(image)
    }
}
