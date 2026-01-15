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
        // Switch to Stable Diffusion XL 1.0 for better quality
        val modelId = "stability.stable-diffusion-xl-v1"
        
        // Stable Diffusion XL Request Format
        val payload = mapOf(
            "text_prompts" to listOf(
                mapOf(
                    "text" to prompt,
                    "weight" to 1.0
                ),
                mapOf(
                    "text" to "bad quality, low resolution, blurry, distorted, nsfw", // Negative prompt
                    "weight" to -1.0
                )
            ),
            "cfg_scale" to 10,
            "steps" to 30,
            "seed" to (seed ?: (0..2147483647).random()),
            "width" to width,
            "height" to height,
            "samples" to 1
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
            
            // Parse response (SDXL returns "artifacts": [{"base64": "..."}])
            val responseJson = objectMapper.readTree(responseBody)
            val base64Image = responseJson.get("artifacts").get(0).get("base64").asText()
            
            return base64Image

        } catch (e: Exception) {
            logger.error("Bedrock generation failed", e)
            throw RuntimeException("Failed to generate image via Bedrock: ${e.message}")
        }
    }
}
