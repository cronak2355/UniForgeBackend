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
        val modelId = "amazon.titan-image-generator-v2:0"
        
        // Titan Image Generator v1 Request Format
        val payload = mapOf(
            "taskType" to "TEXT_IMAGE",
            "textToImageParams" to mapOf(
                "text" to prompt,
                "negativeText" to "bad quality, low resolution, blurry" // Default negative prompt
            ),
            "imageGenerationConfig" to mapOf(
                "numberOfImages" to 1,
                "height" to height,
                "width" to width,
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
            
            // Parse response (Titan returns "images": [base64_string, ...])
            val responseJson = objectMapper.readTree(responseBody)
            val base64Image = responseJson.get("images").get(0).asText()
            
            return base64Image

        } catch (e: Exception) {
            logger.error("Bedrock generation failed", e)
            throw RuntimeException("Failed to generate image via Bedrock: ${e.message}")
        }
    }
}
