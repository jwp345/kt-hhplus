package com.hhplus.presentation

import com.hhplus.domain.exception.InvalidException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun <T> handleIllegalStateException(ex: InvalidException): ApiResponse<T?> {
        return ApiResponse.error(code = ex.code, message = ex.message)
    }

    @ExceptionHandler
    fun <T> handleGlobalException(ex : Exception): ResponseEntity<T> {
        ex.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}