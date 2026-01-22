package com.unifor.backend.controller

import com.unifor.backend.image.service.TranslationService
import com.unifor.backend.image.service.BedrockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
// @RequestMapping("/api") - Removed to avoid duplicate prefix with context-path
class AiController(
    private val bedrockService: BedrockService,
    private val translationService: TranslationService
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
    @PostMapping("/ai/translate")
    fun translate(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        val text = request["text"] ?: return ResponseEntity.badRequest().build()
        val targetLang = request["targetLang"] ?: "en"
        
        return try {
            val translatedText = translationService.translate(text)
            ResponseEntity.ok(mapOf("translatedText" to translatedText))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Translation failed")))
        }
    }

    @PostMapping("/generate-animation-sheet")
    fun generateAnimationSheet(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val prompt = request["prompt"] as? String ?: return ResponseEntity.badRequest().build()
        val image = request["image"] as? String ?: return ResponseEntity.badRequest().build()
        val seed = (request["seed"] as? Number)?.toLong()

        return try {
            val resultImages = bedrockService.generateAnimationSheet(prompt, image, seed)
            ResponseEntity.ok(mapOf(
                "images" to resultImages,  // Changed: array of 4 images for frontend stitching
                "seed" to (seed ?: 0)
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Animation generation failed")))
        }
    }
}
