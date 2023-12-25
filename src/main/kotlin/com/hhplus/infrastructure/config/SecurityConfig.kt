package com.hhplus.infrastructure.config

import com.hhplus.infrastructure.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf {
                disable()
            }
            headers {
                frameOptions {
                    sameOrigin = true
                }
            }
            formLogin {
                disable()
            }
            authorizeRequests {
                authorize("/swagger-ui/**", permitAll)
                authorize("/api/v1/token", permitAll)
                authorize(anyRequest, "SCOPE_booking")
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
        http.addFilterAt(
            JwtAuthenticationFilter(),
                BasicAuthenticationFilter::class.java
        )
        return http.build()!!
    }
}