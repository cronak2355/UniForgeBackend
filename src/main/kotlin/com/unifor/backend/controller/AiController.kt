package com.unifor.backend.controller

import com.unifor.backend.image.service.BedrockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
// @RequestMapping("/api") - Removed to avoid duplicate prefix with context-path
class AiController(
    private val bedrockService: BedrockService
) {

    @PostMapping("/AIgenerate")
    fun generateImage(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val prompt = request["prompt"] as? String ?: return ResponseEntity.badRequest().build()
        val size = (request["size"] as? Int) ?: 512
        
        // Bedrock (Titan) supports 512x512, 1024x1024, etc.
        // We ensure it's a supported size or default to 512
        val validSize = if (size >= 1024) 1024 else 512

        return try {
            val base64Image = bedrockService.generateImage(prompt, width = validSize, height = validSize)
            ResponseEntity.ok(mapOf(
                "image" to base64Image,
                "seed" to 0 // Bedrock might not return the seed easily unless we pass it, handled in service
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }

    @PostMapping("/generate-animation-frame")
    fun generateAnimationFrame(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val prompt = request["prompt"] as? String ?: return ResponseEntity.badRequest().build()
        val seed = (request["seed"] as? Number)?.toLong()
        val size = (request["size"] as? Int) ?: 512
        val validSize = if (size >= 1024) 1024 else 512

        return try {
            val base64Image = bedrockService.generateImage(prompt, seed = seed, width = validSize, height = validSize)
            ResponseEntity.ok(mapOf(
                "image" to base64Image,
                "seed" to (seed ?: 0)
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }

    @PostMapping("/remove-background")
    fun removeBackground(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val base64Image = request["image"] as? String ?: return ResponseEntity.badRequest().build()

        return try {
            val processedImage = bedrockService.removeBackground(base64Image)
            ResponseEntity.ok(mapOf(
                "image" to processedImage
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }
}
