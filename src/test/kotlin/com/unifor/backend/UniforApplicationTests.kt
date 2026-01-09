package com.unifor.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.connection.RedisConnectionFactory
import software.amazon.awssdk.services.s3.S3Client

@SpringBootTest
@EnableAutoConfiguration(exclude = [RedisAutoConfiguration::class, RedisRepositoriesAutoConfiguration::class])
@org.junit.jupiter.api.Disabled("Requires local redis/s3 setup")
class UniforApplicationTests {

    @MockBean
    lateinit var s3Client: S3Client

    @MockBean
    lateinit var redisConnectionFactory: RedisConnectionFactory

    @Test
    fun contextLoads() {
    }

}
