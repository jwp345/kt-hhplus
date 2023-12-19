package com.hhplus.common

import com.hhplus.exception.InvalidInputException
import com.hhplus.controller.ApiResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun <T> handleIllegalStateException(ex: InvalidInputException): ApiResponse<T?> {
        return ApiResponse.error(code = ex.code, message = ex.message)
    }
}