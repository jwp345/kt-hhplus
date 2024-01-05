package com.hhplus.domain.exception

abstract class FailedException : RuntimeException() {
    abstract val code: Int
}