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
    
    // Client is lazy initialized to ensure region is picked up or credentials are ready
    private val client by lazy {
        BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .build()
    }

    fun generateImage(prompt: String, seed: Long? = null, width: Int = 512, height: Int = 512): String {
        // Switch to Amazon Nova Canvas for high quality (Drop-in replacement for Titan)
        val modelId = "amazon.nova-canvas-v1:0"
        
        // Nova Canvas / Titan v2 Request Format
        val payload = mapOf(
            "taskType" to "TEXT_IMAGE",
            "textToImageParams" to mapOf(
                // Enforce single subject constraint
                "text" to "solo, single isolated subject, centered, $prompt",
                "negativeText" to "multiple, two, group, crowd, duplicate, many, extra limbs, bad quality, low resolution, blurry, distorted" 
            ),
            "imageGenerationConfig" to mapOf(
                "numberOfImages" to 1,
                "height" to 1024, // Nova Canvas works best at 1024+
                "width" to 1024,
                "cfgScale" to 8.0,
                "quality" to "standard", // Nova specific
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
}
