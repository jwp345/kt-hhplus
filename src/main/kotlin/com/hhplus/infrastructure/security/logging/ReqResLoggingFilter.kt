package com.hhplus.infrastructure.security.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.*

@Component
class ReqResLoggingFilter : OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val cachingRequestWrapper = RepeatableContentCachingRequestWrapper(request)
        val cachingResponseWrapper = ContentCachingResponseWrapper(response)

        filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper)
        val requestId = UUID.randomUUID().toString().substring(0, 8)

        MDC.put(REQUEST_ID, requestId)
        try {
            log.info {
                HttpLogMessage.createInstance(
                    requestWrapper = cachingRequestWrapper,
                    responseWrapper = cachingResponseWrapper,
                    traceId = requestId
                ).toPrettierLog()
            }

            cachingResponseWrapper.copyBodyToResponse()
        } catch (e: Exception) {
            log.error(e) { "[${this::class.simpleName}] Logging 실패" }
        }

        MDC.remove(REQUEST_ID)
    } companion object {
        const val REQUEST_ID = "request_id"
    }
}