package com.uniforge.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UniforApplication

fun main(args: Array<String>) {
    runApplication<UniforApplication>(*args)
}
