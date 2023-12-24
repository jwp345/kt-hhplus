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

    /* TODO : 토큰 검증
    *   토큰 검증은 스프링 시큐리티에서 시행하며 user 생성 시 UUID도 같이 생성되고
    * UUID 생성 시 레디스에도 MAP<UUID, USERID>정보를 적재한 후 해당 토큰의 UUID를 까서
    * 유효성 검증 및 USERID를 언제든 가져온다.
    * */
    @PostMapping("")
    fun generateToken(@RequestParam(required = true) uuid: Long) : ApiResponse<WaitTokenResponse> {
        val waitToken : WaitToken = tokenProvider.createToken(uuid = uuid)
        return ApiResponse.ok(WaitTokenResponse(userUUID = waitToken.uuid, waitOrder = waitToken.order,
            token = waitToken.token))
    }

}