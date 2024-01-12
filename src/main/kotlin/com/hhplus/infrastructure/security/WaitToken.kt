package com.hhplus.infrastructure.security

import java.io.Serializable
import java.time.LocalDateTime

data class WaitToken(val uuid : Long, val order : Long, val createAt : Long) : Serializable