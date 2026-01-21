package com.unifor.backend.image.service

import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

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
        val modelId = "stability.stable-image-ultra-v1:1"
        val translatedPrompt = translationService.translate(prompt)
        
        logger.info("[BedrockService] Input: $prompt -> Translated: $translatedPrompt")
        
        val payload = mapOf(
            "prompt" to "pixel art style, full body, head to toe, standing pose, solo, single isolated subject, centered, white background, simple background, $translatedPrompt",
            "negative_prompt" to "text, watermark, low quality, cropped, worst quality, detailed background, complex background, noise, dithering",
            "mode" to "text-to-image",
            "aspect_ratio" to "2:3",
            "output_format" to "png",
            "seed" to (seed ?: (0..2147483647).random())
        )

        try {
            val request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(objectMapper.writeValueAsString(payload)))
                .build()

            val response = client.invokeModel(request)
            val responseJson = objectMapper.readTree(response.body().asUtf8String())
            return responseJson.get("images").get(0).asText()

        } catch (e: Exception) {
            logger.error("Bedrock generation failed", e)
            if (e.message?.contains("content filters") == true) {
                throw RuntimeException("이미지 생성이 차단되었습니다. 저작권이 있는 캐릭터나 부적절한 키워드는 사용할 수 없습니다.")
            }
            throw RuntimeException("Failed to generate image: ${e.message}")
        }
    }

    fun removeBackground(base64Image: String): String {
        // Return original image - let frontend handle background removal client-side
        // This avoids dependency on unstable Nova Canvas API
        logger.info("[BedrockService] Background removal delegated to frontend")
        return base64Image
    }

    /**
     * Generate 4 animation frames as separate images.
     * Frontend will stitch them together.
     */
    fun generateAnimationSheet(prompt: String, base64Image: String, seed: Long? = null): List<String> {
        val modelId = "amazon.titan-image-generator-v1"
        val translatedPrompt = translationService.translate(prompt)
        
        logger.info("[BedrockService] Animation: $prompt -> $translatedPrompt")

        val payload = mapOf(
            "taskType" to "IMAGE_VARIATION",
            "imageVariationParams" to mapOf(
                "text" to "character action sequence, side view, white background, $translatedPrompt",
                "images" to listOf(base64Image)
            ),
            "imageGenerationConfig" to mapOf(
                "numberOfImages" to 4,
                "height" to 512,
                "width" to 512,
                "cfgScale" to 8.0,
                "seed" to (seed ?: (0..2147483647).random())
            )
        )

        try {
            val request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(objectMapper.writeValueAsString(payload)))
                .build()

            val response = client.invokeModel(request)
            val responseJson = objectMapper.readTree(response.body().asUtf8String())
            
            if (responseJson.has("images")) {
                val imageList = mutableListOf<String>()
                val imagesNode = responseJson.get("images")
                for (i in 0 until imagesNode.size()) {
                    imageList.add(imagesNode.get(i).asText())
                }
                return imageList
            } else {
                throw RuntimeException("Titan response missing 'images' field")
            }

        } catch (e: Exception) {
            logger.error("Animation generation failed", e)
            throw RuntimeException("Animation generation failed: ${e.message}")
        }
    }
}

