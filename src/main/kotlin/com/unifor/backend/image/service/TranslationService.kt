index-BVt4BKlY.js:8 Unable to preventDefault inside passive event listener invocation.package com.unifor.backend.image.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory

@Service
class TranslationService(
    @Value("\${aws.bedrock.region:us-east-1}") private val region: String
) {
    private val logger = LoggerFactory.getLogger(TranslationService::class.java)
    private val objectMapper = jacksonObjectMapper()
    
    // AWS Bedrock Client (reusing infrastructure)
    private val client by lazy {
        BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .build()
    }

    /**
     * Translates text to English using Bedrock LLM (Claude 3 Haiku).
     * This bypasses AWS Translate service permissions.
     */
    fun translate(text: String): String {
        if (text.isBlank()) return ""
        
        // If text already seems to be English (simple check), just return
        if (text.all { it.code < 128 }) return text

        val modelId = "anthropic.claude-3-haiku-20240307-v1:0"

        try {
            val prompt = "Translate the following text to English. Output ONLY the translated text, no introductory words.\n\nText: $text"
            
            val payload = mapOf(
                "anthropic_version" to "bedrock-2023-05-31",
                "max_tokens" to 200,
                "messages" to listOf(
                    mapOf(
                        "role" to "user",
                        "content" to listOf(
                            mapOf(
                                "type" to "text",
                                "text" to prompt
                            )
                        )
                    )
                )
            )

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
            
            // Claude 3 response structure
            val content = responseJson.get("content").get(0).get("text").asText()
            return content.trim()

        } catch (e: Exception) {
            logger.error("Bedrock Translation failed: ${e.message}", e)
            return text // Fallback to original
        }
    }
}
