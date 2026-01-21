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

@Service
class BedrockService(
    @Value("\${aws.bedrock.region:us-east-1}") private val region: String,
    private val translationService: TranslationService // Injected
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
        // Use specific version tag for SDXL to avoid EOL error
        val modelId = "stability.stable-diffusion-xl-v1:0"
        
        // Translate prompt
        val translatedPrompt = translationService.translate(prompt)
        logger.info("[BedrockService] Animation Prompt: $prompt -> $translatedPrompt")

        // Construct Payload for SDXL Image-to-Image
        val finalPrompt = "sprite sheet, 4 frames sequence, consecutive action, side view, same character, white background, simple background, $translatedPrompt"
        
        val payload = mapOf(
            "text_prompts" to listOf(
                mapOf("text" to finalPrompt, "weight" to 1.0),
                mapOf("text" to "text, watermark, low quality, detailed background, complex background, blurry, distorted", "weight" to -1.0)
            ),
            "init_image" to base64Image,
            "cfg_scale" to 10,
            "steps" to 30,
            "style_preset" to "pixel-art", // SDXL supports style presets
            "image_strength" to 0.35, // Lower means more influence from init_image (0.35 is good for keeping shape but animating) - check SDXL docs 
            // NOTE: Bedrock SDXL init_image_strength: 0.0 to 1.0. 
            // But wait, for SDXL, 'image_strength' controls how much to change? 
            // usually strength 0.3 means 30% change? Or 30% original?
            // "image_strength" (float) – The proportion of the generation that is based on the initial image. 
            // 0.0 to 1.0. A value closer to 1.0 creates an image that is more similar to the initial image.
            // So we want high similarity? No, we want animation.
            // Let's try 0.6
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
            val responseJson = objectMapper.readTree(responseBody)
            
            // SDXL returns { "artifacts": [ { "base64": "..." } ] }
            return responseJson.get("artifacts").get(0).get("base64").asText()

        } catch (e: Exception) {
            logger.error("Bedrock animation generation failed", e)
            throw RuntimeException("Animation generation failed: ${e.message}")
        }
    }
}
