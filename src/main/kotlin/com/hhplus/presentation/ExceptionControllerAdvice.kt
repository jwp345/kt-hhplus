package com.hhplus.presentation

import com.hhplus.domain.exception.InvalidException
import mu.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice() {

    private val log = KotlinLogging.logger("GlobalExceptionHandler")
    @ExceptionHandler
    fun <T> handleIllegalStateException(ex: InvalidException): ApiResponse<T?> {
        return ApiResponse.error(code = ex.code, message = ex.message)
    }

    @ExceptionHandler
    fun <T> handleGlobalException(ex : Exception): ApiResponse<T?> {
        log.error { ex.stackTrace }
        return ApiResponse.error(code = 500, message = "알 수 없는 에러가 발생 하였습니다.")
    }
}