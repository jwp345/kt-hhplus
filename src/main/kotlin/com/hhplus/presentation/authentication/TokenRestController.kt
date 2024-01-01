package com.hhplus.presentation.authentication

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.TokenProvider
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/token")
class TokenRestController(val tokenProvider : TokenProvider) {

    @PostMapping("")
    fun generateToken(@RequestBody waitTokenRequest: WaitTokenRequest) : ApiResponse<WaitTokenResponse> {
        tokenProvider.createToken(uuid = waitTokenRequest.uuid).let { token ->
            return ApiResponse.ok(WaitTokenResponse(token = token))
        }
    }

}