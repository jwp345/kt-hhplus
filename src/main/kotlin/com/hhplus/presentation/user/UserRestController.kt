package com.hhplus.presentation.user

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.UserFacade
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(val userFacade: UserFacade) {

    /* TODO : 시큐리티 구성 후 접속 정보(uuid)와 요청하는 user UUId 값이 같은지 확인 */
    @PostMapping("/{uuid}/charge")
    fun chargeMoney(@PathVariable uuid : Long, @RequestParam("amount") amount : Long) : ApiResponse<UserBalanceResponse> {
        return ApiResponse.ok(userFacade.chargeMoney(uuid = uuid, amount = amount)
            .let { user ->
            UserBalanceResponse(userName = user.name, balance = user.balance)
        })
    }

    @GetMapping("/{uuid}/check/balance")
    fun checkBalance(@PathVariable uuid: Long) : ApiResponse<UserBalanceResponse> {
        return userFacade.checkBalance(uuid = uuid).let { user ->
            ApiResponse.ok(UserBalanceResponse(userName = user.name, balance = user.balance))
        }
    }
}