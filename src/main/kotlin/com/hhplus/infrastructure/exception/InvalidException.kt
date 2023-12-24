package com.hhplus.infrastructure.exception

abstract class InvalidException : IllegalArgumentException() {
    abstract val code: Int
}