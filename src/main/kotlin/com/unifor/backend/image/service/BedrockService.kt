package com.unifor.backend.image.service

import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import java.util.Base64
import org.springframework.beans.factory.annotation.Value
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class BedrockService(
    @Value("\${aws.bedrock.region:us-east-1}") private val region: String,
    private val translationService: TranslationService
) {
    private val logger = LoggerFactory.getLogger(BedrockService::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val client by lazy {
        BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .build()
    }

    fun generateImage(prompt: String, seed: Long? = null, width: Int = 512, height: Int = 512): String {
        // Upgrade to Stable Image Ultra (SD3.5 Large) in Oregon (us-west-2)
        // High quality pixel art generation model
        val modelId = "stability.stable-image-ultra-v1:1"
        
        // Use AWS Translate service for full translation capability
        val translatedPrompt = translationService.translate(prompt)
        
        logger.info("[BedrockService] Input Prompt: $prompt")
        logger.info("[BedrockService] Translated Prompt: $translatedPrompt")
        
        // Stable Image Ultra / SD3 Payload
        val payload = mapOf(
            "prompt" to "pixel art style, full body, head to toe, standing pose, solo, single isolated subject, centered, white background, simple background, $translatedPrompt",
            "negative_prompt" to "text, watermark, low quality, cropped, worst quality, detailed background, complex background, noise, dithering",
            "mode" to "text-to-image",
            "aspect_ratio" to "2:3",
            "output_format" to "png",
            "seed" to (seed ?: (0..2147483647).random())
        )

        val jsonBody = objectMapper.writeValueAsString(payload)

        try {
            val request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(jsonBody))
                .build()

            val response = client.invokeModel(request)
            val responseBody = response.body().asUtf8String()
            
            // Ultra returns { "images": [ "base64..." ] }
            val responseJson = objectMapper.readTree(responseBody)
            val base64Image = responseJson.get("images").get(0).asText()
            
            return base64Image

        } catch (e: Exception) {
            logger.error("Bedrock generation failed", e)
            val errorMessage = e.message ?: "Unknown error"
            
            if (errorMessage.contains("content filters")) {
                throw RuntimeException("이미지 생성이 차단되었습니다. 저작권이 있는 캐릭터나 부적절한 키워드는 사용할 수 없습니다.")
            }
            
            throw RuntimeException("Failed to generate image via Bedrock: $errorMessage")
        }
    }

    fun removeBackground(base64Image: String): String {
        // Attempt Nova Canvas for Background Removal
        // If it fails (e.g. model unavailable or 500), return original image to trigger frontend fallback
        val modelId = "amazon.nova-canvas-v1:0"
        
        val payload = mapOf(
            "taskType" to "BACKGROUND_REMOVAL",
            "backgroundRemovalParams" to mapOf(
                "image" to base64Image
            )
        )

        try {
            val jsonBody = objectMapper.writeValueAsString(payload)
            
            val request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json") 
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(jsonBody))
                .build()

            val response = client.invokeModel(request)
            val responseBody = response.body().asUtf8String()
            
            val responseJson = objectMapper.readTree(responseBody)
            
            if (responseJson.has("image")) {
                return responseJson.get("image").asText()
            } else {
                 logger.warn("Nova Canvas returned unexpected format. Returning original image.")
                 return base64Image
            }

        } catch (e: Exception) {
            logger.warn("Bedrock background removal failed (Model: $modelId): ${e.message}. Returning original image for frontend fallback.")
            return base64Image
        }
    }

    fun generateAnimationSheet(prompt: String, base64Image: String, seed: Long? = null): String {
        // Amazon Titan Image Generator v1
        val modelId = "amazon.titan-image-generator-v1"
        
        // Translate prompt
        val translatedPrompt = translationService.translate(prompt)
        logger.info("[BedrockService] Animation Prompt (Titan): $prompt -> $translatedPrompt")

        // Construct Payload for Titan Image Variation
        // We request 4 variations to create a 4-frame sprite sheet
        val finalPrompt = "sprite sheet, character action sequence, side view, white background, $translatedPrompt"
        
        val payload = mapOf(
            "taskType" to "IMAGE_VARIATION",
            "imageVariationParams" to mapOf(
                "text" to finalPrompt,
                "images" to listOf(base64Image)
            ),
            "imageGenerationConfig" to mapOf(
                "numberOfImages" to 4, // Generate 4 frames
                "height" to 512,
                "width" to 512,
                "cfgScale" to 8.0,
                "seed" to (seed ?: (0..2147483647).random())
            )
        )

        val jsonBody = objectMapper.writeValueAsString(payload)

        try {
            val request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(jsonBody))
                .build()

            val response = client.invokeModel(request)
            val responseBody = response.body().asUtf8String()
            val responseJson = objectMapper.readTree(responseBody)
            
            if (responseJson.has("images")) {
                val imagesNode = responseJson.get("images")
                if (imagesNode.size() == 4) {
                    // Stitch 4 images horizontally
                    val stitchedImage = BufferedImage(2048, 512, BufferedImage.TYPE_INT_ARGB)
                    val g = stitchedImage.createGraphics()
                    
                    for (i in 0 until 4) {
                        val base64Frame = imagesNode.get(i).asText()
                        val frameBytes = Base64.getDecoder().decode(base64Frame)
                        val frameImg = ImageIO.read(ByteArrayInputStream(frameBytes))
                        g.drawImage(frameImg, i * 512, 0, null)
                    }
                    g.dispose()
                    
                    // Convert back to Base64
                    val os = ByteArrayOutputStream()
                    ImageIO.write(stitchedImage, "png", os)
                    return Base64.getEncoder().encodeToString(os.toByteArray())
                    
                } else {
                    // Fallback if less than 4 images
                     return imagesNode.get(0).asText()
                }
            } else {
                throw RuntimeException("Titan response missing 'images' field: $responseBody")
            }

        } catch (e: Exception) {
            logger.error("Bedrock animation generation failed", e)
            throw RuntimeException("Animation generation failed: ${e.message}")
        }
    }
}
