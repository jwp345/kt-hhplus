package com.hhplus.presentation

import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T,
) {
    companion object {

        private fun <T> of(code: Int, message: String?, data: T): ApiResponse<T> {
            return ApiResponse(
                code = code,
                message = message,
                data = data,
            )
        }

        fun <T> ok(data: T): ApiResponse<T> {
            return of(HttpStatus.OK.value(), null, data)
        }

        fun <T> error(code: Int, message: String?): ApiResponse<T?> {
            return of(code, message, null)
        }

        fun<T> globalError() : ApiResponse<T?> {
            return of(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null)
        }
    }
}
