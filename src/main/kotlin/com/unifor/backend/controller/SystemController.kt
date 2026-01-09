package com.unifor.backend.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    @Value("\${COMMIT_HASH:unknown}")
    private val commitHash: String
) {
    @GetMapping("/system/version")
    fun getVersion(): Map<String, String> {
        return mapOf(
            "commitHash" to commitHash,
            "buildTime" to (System.getenv("BUILD_TIME") ?: "unknown")
        )
    }
}
