package com.hhplus.controller

import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T,
) {
    companion object {
        private fun <T> of(status: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(
                code = status.value(),
                message = message,
                data = data,
            )
        }

        private fun <T> of(status: HttpStatus, data: T): ApiResponse<T> {
            return of(status, status.name, data)
        }

        fun <T> ok(data: T): ApiResponse<T> {
            return of(HttpStatus.OK, data)
        }

        fun <T> error(code: Int, message: String): ApiResponse<T?> {
            return ApiResponse(code = code,
                message = message,
                null)
        }
    }
}
