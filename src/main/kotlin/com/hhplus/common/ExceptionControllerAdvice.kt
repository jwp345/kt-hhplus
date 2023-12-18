package com.hhplus.common

import com.hhplus.controller.ApiResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun <T> handleIllegalStateException(ex: InvalidInputException): ApiResponse<T?> {
        return ApiResponse.error(code = ex.code, message = ex.message)
    }
}