package com.ralabs.security.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder




@SpringBootApplication
class DemoApplication {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }

}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
