package com.hhplus.common

abstract class InvalidInputException : IllegalArgumentException() {
    abstract val code: Int
}