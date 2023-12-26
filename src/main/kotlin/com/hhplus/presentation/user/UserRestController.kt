package com.hhplus.presentation.user

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.UserFacade
import com.hhplus.component.UserValidator
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(val userFacade: UserFacade, val userValidator: UserValidator) {

    @PostMapping("/{uuid}/charge")
    fun chargeMoney(@PathVariable uuid : Long, @RequestParam("amount") amount : Long) : ApiResponse<UserBalanceResponse> {
        userValidator.checkTokenAndUser(userUuid = uuid)
        return ApiResponse.ok(userFacade.chargeMoney(uuid = uuid, amount = amount)
            .let { user ->
            UserBalanceResponse(userName = user.name, balance = user.balance)
        })
    }

    @GetMapping("/{uuid}/check/balance")
    fun checkBalance(@PathVariable uuid: Long) : ApiResponse<UserBalanceResponse> {
        userValidator.checkTokenAndUser(userUuid = uuid)
        return userFacade.checkBalance(uuid = uuid).let { user ->
            ApiResponse.ok(UserBalanceResponse(userName = user.name, balance = user.balance))
        }
    }
}