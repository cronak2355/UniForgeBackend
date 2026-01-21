package com.unifor.backend.image.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.translate.TranslateClient
import software.amazon.awssdk.services.translate.model.TranslateTextRequest
import org.slf4j.LoggerFactory

@Service
class TranslationService(
    @Value("\${aws.s3.region:ap-northeast-2}") private val region: String
) {
    private val logger = LoggerFactory.getLogger(TranslationService::class.java)
    
    // AWS Translate Client (Same region as Bedrock or default)
    private val client by lazy {
        TranslateClient.builder()
            .region(Region.of(region))
            .build()
    }

    /**
     * Translates text from Source Language (default: auto) to Target Language (default: en).
     */
    fun translate(text: String, sourceLang: String = "auto", targetLang: String = "en"): String {
        if (text.isBlank()) return ""

        try {
            val request = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode(sourceLang)
                .targetLanguageCode(targetLang)
                .build()

            val response = client.translateText(request)
            return response.translatedText()
            
        } catch (e: Exception) {
            logger.error("AWS Translate failed: ${e.message}", e)
            // Fallback: Return original text if translation fails
            return text
        }
    }
}
