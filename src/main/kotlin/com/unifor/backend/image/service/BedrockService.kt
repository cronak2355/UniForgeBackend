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
     * Generate 4 animation frames using Stable Image Ultra.
     * Creates walking animation with different action prompts.
     */
    fun generateAnimationSheet(prompt: String, base64Image: String, seed: Long? = null): List<String> {
        // Use same working model as image generation
        val modelId = "stability.stable-image-ultra-v1:1"
        val translatedPrompt = translationService.translate(prompt)
        
        logger.info("[BedrockService] Animation: $prompt -> $translatedPrompt")

        // Define 2 frames for animation (reduced to avoid CloudFront timeout)
        val framePoses = listOf(
            "standing pose, arms at sides",
            "walking pose, leg forward, arm swinging"
        )
        
        val baseSeed = seed ?: (0..2147483647).random().toLong()
        val imageList = mutableListOf<String>()

        for ((index, pose) in framePoses.withIndex()) {
            val payload = mapOf(
                "prompt" to "pixel art style, side view, $pose, white background, $translatedPrompt",
                "negative_prompt" to "text, watermark, low quality, multiple characters, complex background",
                "mode" to "text-to-image",
                "aspect_ratio" to "1:1",
                "output_format" to "png",
                "seed" to (baseSeed + index)
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
                imageList.add(responseJson.get("images").get(0).asText())
                
                logger.info("[BedrockService] Frame ${index + 1}/2 generated")

            } catch (e: Exception) {
                logger.error("Animation frame $index failed", e)
                throw RuntimeException("Animation generation failed at frame ${index + 1}: ${e.message}")
            }
        }

        return imageList
    }
}

