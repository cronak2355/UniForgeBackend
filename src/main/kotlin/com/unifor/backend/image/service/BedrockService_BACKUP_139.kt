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
    @Value("\${aws.bedrock.region:us-east-1}") private val region: String
) {
    private val logger = LoggerFactory.getLogger(BedrockService::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val client by lazy {
        BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .build()
    }

    private val translationMap = mapOf(
        "해골기사" to "Skeleton Knight",
        "픽셀 아트" to "pixel art",
        "호러" to "horror",
        "웅장한" to "epic, grand",
        "기사" to "knight",
        "전사" to "warrior",
        "마법사" to "mage",
        "몬스터" to "monster",
        "배경 제거" to "background removal"
    )

    private fun translatePrompt(prompt: String): String {
        var translated = prompt
        translationMap.forEach { (ko, en) ->
            translated = translated.replace(ko, en)
        }
        return translated
    }

    fun generateImage(prompt: String, seed: Long? = null, width: Int = 512, height: Int = 512): String {
        // Switch to Amazon Nova Canvas for high quality (Drop-in replacement for Titan)
        val modelId = "amazon.nova-canvas-v1:0"
        
        val translatedPrompt = translatePrompt(prompt)
        
        // Nova Canvas / Titan v2 Request Format
        val payload = mapOf(
            "taskType" to "TEXT_IMAGE",
            "textToImageParams" to mapOf(
<<<<<<< HEAD
                // Enforce single subject constraint
                "text" to "solo, single isolated subject, centered, white background, simple background, $prompt",
                "negativeText" to "multiple, two, group, crowd, duplicate, many, extra limbs, bad quality, low resolution, blurry, distorted" 
=======
                // Enforce single subject constraint and style
                "text" to "pixel art style, solo, single isolated subject, centered, $translatedPrompt",
                "negativeText" to "multiple, two, group, crowd, duplicate, many, extra limbs, bad quality, low resolution, blurry, distorted, nsfw, text, watermark, plain background" 
>>>>>>> dev
            ),
            "imageGenerationConfig" to mapOf(
                "numberOfImages" to 1,
                "height" to 512, // User usually works at 512 in editor
                "width" to 512,
                "cfgScale" to 8.5, // Increased for better prompt adherence
                "quality" to "standard",
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
            
            // Parse response (Nova Canvas returns "images": [base64_string, ...])
            val responseJson = objectMapper.readTree(responseBody)
            val base64Image = responseJson.get("images").get(0).asText()
            
            return base64Image

        } catch (e: Exception) {
            logger.error("Bedrock generation failed", e)
            val errorMessage = e.message ?: "Unknown error"
            
            if (errorMessage.contains("content filters")) {
                throw RuntimeException("이미지 생성이 차단되었습니다. '마리오(Mario)'와 같은 저작권이 있는 캐릭터나 부적절한 키워드는 생성할 수 없습니다. 다른 프롬프트로 시도해 주세요.")
            }
            
            throw RuntimeException("Failed to generate image via Bedrock: $errorMessage")
        }
    }

    fun removeBackground(base64Image: String): String {
        val modelId = "amazon.nova-canvas-v1:0"
        
        val payload = mapOf(
            "taskType" to "BACKGROUND_REMOVAL",
            "backgroundRemovalParams" to mapOf(
                "image" to base64Image
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
            return responseJson.get("images").get(0).asText()

        } catch (e: Exception) {
            logger.error("Bedrock background removal failed", e)
            throw RuntimeException("Failed to remove background via Bedrock: ${e.message}")
        }
    }
}
