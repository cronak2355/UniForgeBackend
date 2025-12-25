package com.unifor.backend.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate,
    private val redisTemplate: StringRedisTemplate
) {

    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val status = mutableMapOf<String, Any>("status" to "UP")

        // PostgreSQL 체크
        try {
            val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            status["database"] = "Connected to PostgreSQL (Result: $result)"
        } catch (e: Exception) {
            status["database"] = "FAILED: ${e.message}"
            status["status"] = "DOWN"
        }

        // Redis 체크
        try {
            redisTemplate.opsForValue().set("health_check", "ok")
            val value = redisTemplate.opsForValue().get("health_check")
            status["redis"] = "Connected to Redis (Value: $value)"
        } catch (e: Exception) {
            status["redis"] = "FAILED: ${e.message}"
            status["status"] = "DOWN"
        }

        return ResponseEntity.ok(status)
    }
}
