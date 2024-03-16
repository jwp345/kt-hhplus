package com.hhplus.infrastructure.security.logging

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.IOException


class RepeatableContentCachingRequestWrapper(request: HttpServletRequest?) : ContentCachingRequestWrapper(request!!) {
    private var inputStream: SimpleServletInputStream? = null

    @Throws(IOException::class)
    fun readInputAndDuplicate(): String {
        if (inputStream == null) {
            val body = super.getInputStream().readAllBytes()
            this.inputStream = SimpleServletInputStream(body)
        }
        return String(super.getContentAsByteArray())
    }
}