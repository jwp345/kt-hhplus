package com.hhplus.controller

import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    val code: Int,
    val status: HttpStatus,
    val message: String?,
    val data: T,
) {
    companion object {
        fun <T> of(status: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(
                code = status.value(),
                status = status,
                message = message,
                data = data,
            )
        }

        fun <T> of(status: HttpStatus, data: T): ApiResponse<T> {
            return of(status, status.name, data)
        }

        fun <T> ok(data: T): ApiResponse<T> {
            return of(HttpStatus.OK, data)
        }
    }
}
