package com.hhplus.exception

abstract class InvalidInputException : IllegalArgumentException() {
    abstract val code: Int
}