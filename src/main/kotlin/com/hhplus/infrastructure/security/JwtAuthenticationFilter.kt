package com.hhplus.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(private val tokenProvider: TokenProvider)
    : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        parseBearerToken(request).let { token ->

        }
        filterChain.doFilter(request, response)
    }

    private fun parseBearerToken(request: HttpServletRequest) = request.getHeader(HttpHeaders.AUTHORIZATION)
        .takeIf { it?.startsWith("Bearer ", true) ?: false }?.substring(7)
}