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
        // Upgrade to Stable Image Ultra (SD3.5 Large) in Oregon (us-west-2)
        // High quality pixel art generation model
        val modelId = "stability.stable-image-ultra-v1:1"
        
        val translatedPrompt = translatePrompt(prompt)
        
        // Stable Image Ultra / SD3 Payload
        val payload = mapOf(
            "prompt" to "pixel art style, solo, single isolated subject, centered, $translatedPrompt",
            "mode" to "text-to-image",
            "aspect_ratio" to "1:1",
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
        // Amazon Nova Canvas model ID is incorrect or EOL. 
        // Temporarily disabling background removal until a valid Titan Image Generator v2 payload is implemented.
        // Returning original image as fallback to prevent 500 errors.
        logger.warn("Background removal temporarily disabled due to model unavailability.")
        return base64Image
    }
}
