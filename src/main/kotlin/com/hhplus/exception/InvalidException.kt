package com.hhplus.exception

abstract class InvalidException : IllegalArgumentException() {
    abstract val code: Int
}