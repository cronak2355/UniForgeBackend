package com.unifor.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/version")
class VersionController {

    @GetMapping
    fun getVersion(): VersionResponse {
        val version = System.getenv("APP_VERSION") ?: "dev"
        val gitSha = System.getenv("GIT_SHA") ?: System.getenv("COMMIT_HASH")
        return VersionResponse(version = version, gitSha = gitSha)
    }
}

data class VersionResponse(
    val version: String,
    val gitSha: String?
)
