package com.hhplus.presentation

import com.hhplus.infrastructure.exception.InvalidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun <T> handleIllegalStateException(ex: InvalidException): ApiResponse<T?> {
        return ApiResponse.error(code = ex.code, message = ex.message)
    }
}