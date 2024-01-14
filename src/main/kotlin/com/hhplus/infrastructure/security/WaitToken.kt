package com.hhplus.infrastructure.security

import java.io.Serializable

data class WaitToken(val uuid : Long, val order : Long, val createAt : Long) : Serializable {
    constructor() : this(0, 0, 0)
}