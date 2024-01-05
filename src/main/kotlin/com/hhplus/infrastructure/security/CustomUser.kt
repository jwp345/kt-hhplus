package com.hhplus.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUser(
    val token : WaitToken,
    userName: String,
    password: String,
    authorities: Collection<GrantedAuthority>?
) : User(userName, password, authorities)