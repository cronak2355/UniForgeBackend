package com.example.uniforge.controller

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@RestController
@CrossOrigin(origins = ["http://localhost:5173"]) // React 주소 허용
@RequestMapping("/api")
class AiAssetController {

    // 로컬 파이썬 서버 설정
    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:8000")
        // Base64 이미지가 꽤 크므로 버퍼 사이즈를 20MB로 넉넉하게 확장
        .codecs { it.defaultCodecs().maxInMemorySize(20 * 1024 * 1024) }
        .build()

    @PostMapping("/AIgenerate")
    fun generate(@RequestBody request: Map<String, Any>): Map<String, Any> = runBlocking {
        // 로그: 어떤 데이터가 들어왔는지 확인
        println("📥 [Spring] React 요청 수신: prompt=${request["prompt"]}, size=${request["size"]}, img2img=${request.containsKey("init_image")}")

        try {
            // Python FastAPI 서버 호출 (POST 방식)
            // React에서 받은 JSON Body(prompt, init_image, strength 등)를 그대로 토스합니다.
            val response = webClient.post()
                .uri("/api/AIgenerate") // Python 코드의 엔드포인트와 일치시킴
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map::class.java)
                .timeout(Duration.ofSeconds(90)) // GPU 연산 시간을 고려해 타임아웃 90초 설정
                .awaitSingle()

            @Suppress("UNCHECKED_CAST")
            return@runBlocking response as Map<String, Any>

        } catch (e: Exception) {
            println("❌ [연동 에러] Python 서버 연결 실패: ${e.message}")
            return@runBlocking mapOf("error" to "AI Server Error: ${e.message}")
        }
    }
}