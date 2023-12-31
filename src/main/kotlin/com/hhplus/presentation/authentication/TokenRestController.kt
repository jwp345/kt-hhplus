package com.hhplus.presentation.authentication

import com.hhplus.presentation.ApiResponse
import com.hhplus.infrastructure.security.TokenProvider
import com.hhplus.infrastructure.security.WaitToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/token")
class TokenRestController(val tokenProvider : TokenProvider) {

    @PostMapping("")
    fun generateToken(@RequestParam(required = true) uuid: Long) : ApiResponse<WaitTokenResponse> {
        tokenProvider.createToken(uuid = uuid).let { token ->
            return ApiResponse.ok(WaitTokenResponse(token = token))
        }
    }

}