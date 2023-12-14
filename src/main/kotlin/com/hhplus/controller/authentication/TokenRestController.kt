package com.hhplus.controller.authentication

import com.hhplus.controller.ApiResponse
import com.hhplus.security.TokenProvider
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TokenRestController(val tokenProvider : TokenProvider) {

    @PostMapping("/token")
    fun generateToken() : ApiResponse<WaitTokenDto> {
        return ApiResponse.ok(tokenProvider.createToken())
    }

}