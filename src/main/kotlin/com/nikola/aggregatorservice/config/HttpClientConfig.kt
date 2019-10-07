package com.nikola.aggregatorservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class HttpClientConfig {

    @Bean
    fun httpClient(): HttpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(1))
    .build()
}
