package com.hhplus.infrastructure.config

import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

//@Configuration
//class SecurityConfig {
//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        http.invoke {
//            csrf {
//                disable()
//            }
//            headers {
//                frameOptions {
//                    sameOrigin = true
//                }
//            }
//            formLogin {
//                disable()
//            }
//            authorizeRequests {
//                authorize("/swagger-ui/**", permitAll)
//                authorize("/api/v1/token", permitAll)
//                authorize(anyRequest, authenticated)
//            }
//            sessionManagement {
//                sessionCreationPolicy = SessionCreationPolicy.STATELESS
//            }
//        }
//        http.addFilterAt(
//            JwtA
//        )
//        return http.build()!!
//    }
//}