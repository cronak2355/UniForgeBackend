package com.unifor.backend.image.controller

import com.unifor.backend.image.dto.CreateImageResourceRequest
import com.unifor.backend.image.dto.ImageResourceResponse
import com.unifor.backend.image.service.ImageResourceService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/images")
class ImageResourceController(
    private val service: ImageResourceService
) {

    @PostMapping
    fun createImage(
        @RequestBody request: CreateImageResourceRequest
    ): ImageResourceResponse {

        val image = service.create(request)

        return ImageResourceResponse(
            id = image.id,
            ownerType = image.ownerType,
            ownerId = image.ownerId,
            imageType = image.imageType,
            s3Key = image.s3Key,
            createdAt = image.createdAt
        )
    }
}



