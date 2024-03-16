package com.hhplus.infrastructure.security

import com.hhplus.application.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.*


class WaitTokenFilter(private val tokenProvider: TokenProvider)
    : OncePerRequestFilter() {

    private val log = KotlinLogging.logger("BookingProcessor")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token : WaitToken? = decodeToken(request)

        if (token != null && tokenProvider.validateToken(token)) {
            val authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun decodeToken(request: HttpServletRequest): WaitToken? {
        request.getHeader("X-WAIT-TOKEN")
            .let { token ->
                if(token == null) {
                    log.warn("Cannot Find Token In Header")
                    return null
                }

                try {
                    val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(token))
                    ObjectInputStream(inputStream).use {
                        return it.readObject() as? WaitToken
                    }
                } catch (e : Exception) {
                    log.warn("Fail to Decode Token : {}", token.toString())
                    return null
                }
            }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludePath = arrayOf("/api/v1/token")
        val path = request.requestURI
        return Arrays.stream(excludePath).anyMatch { prefix: String? ->
            path.startsWith(
                prefix!!
            )
        }
    }
}