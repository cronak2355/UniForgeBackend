
package com.unifor.backend.image.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.translate.TranslateClient
import software.amazon.awssdk.services.translate.model.TranslateTextRequest
import org.slf4j.LoggerFactory

@Service
class TranslationService(
    @Value("\${aws.bedrock.region:us-east-1}") private val region: String
) {
    private val logger = LoggerFactory.getLogger(TranslationService::class.java)
    
    // AWS Translate Client
    private val client by lazy {
        TranslateClient.builder()
            .region(Region.of(region))
            .build()
    }

    /**
     * Translates text to English using AWS Translate.
     */
    fun translate(text: String): String {
        if (text.isBlank()) return ""
        
        // If text already seems to be English (simple check), just return
        if (text.all { it.code < 128 }) return text

        try {
            val request = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode("auto")
                .targetLanguageCode("en")
                .build()

            val response = client.translateText(request)
            val translatedText = response.translatedText()
            
            logger.info("Translation Success: '$text' -> '$translatedText'")
            return translatedText

        } catch (e: Exception) {
            logger.error("AWS Translate failed: ${e.message}", e)
            logger.warn("Falling back to original text: $text")
            return text 
        }
    }
}
