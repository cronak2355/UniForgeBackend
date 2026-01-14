package com.unifor.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import java.util.Base64
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@RestController
@RequestMapping("/api")
class AiController {

    @PostMapping("/AIgenerate")
    fun generateImage(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        // TODO: Implement actual AI generation logic (e.g., call OpenAI or Stability AI)
        // For now, return a placeholder red square to prove connectivity and fix the 500 error.
        
        println("AI Generation Request: $request")
        
        val prompt = request["prompt"] as? String ?: "No prompt"
        val base64Image = createPlaceholderImage(512, 512, Color.RED, prompt)

        return ResponseEntity.ok(mapOf(
            "image" to base64Image,
            "seed" to 123456
        ))
    }

    @PostMapping("/generate-animation-frame")
    fun generateAnimationFrame(@RequestBody request: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        // TODO: Implement actual animation frame generation
        println("Animation Frame Request: $request")
        
        val prompt = request["prompt"] as? String ?: "No prompt"
        val base64Image = createPlaceholderImage(128, 128, Color.BLUE, prompt)

        return ResponseEntity.ok(mapOf(
            "image" to base64Image,
            "seed" to 123456
        ))
    }

    private fun createPlaceholderImage(width: Int, height: Int, color: Color, text: String): String {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        
        graphics.color = color
        graphics.fillRect(0, 0, width, height)
        
        graphics.color = Color.WHITE
        graphics.drawString("AI Mock", 10, 20)
        // Simple text wrapping or truncation could go here, but keep it simple
        graphics.drawString(text.take(20), 10, 40)
        
        graphics.dispose()

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }
}
