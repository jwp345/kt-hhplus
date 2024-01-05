package com.hhplus.domain.exception

abstract class InvalidException : IllegalArgumentException() {
    abstract val code: Int
}