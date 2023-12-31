package com.hhplus.presentation.user

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.UserFacade
import com.hhplus.component.UserValidator
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(val userFacade: UserFacade, val userValidator: UserValidator) {

    @PostMapping("/charge")
    fun chargeMoney(@RequestBody chargeMoneyCommand: ChargeMoneyCommand) : ApiResponse<UserBalanceResponse> {
        userValidator.checkTokenAndUser(userUuid = chargeMoneyCommand.uuid)
        return ApiResponse.ok(userFacade.chargeMoney(uuid = chargeMoneyCommand.uuid, amount = chargeMoneyCommand.amount)
            .let { user ->
            UserBalanceResponse(userName = user.name, balance = user.balance)
        })
    }

    @GetMapping("/check/balance")
    fun checkBalance(@RequestBody checkBalanceRequest: CheckBalanceRequest) : ApiResponse<UserBalanceResponse> {
        userValidator.checkTokenAndUser(userUuid = checkBalanceRequest.uuid)
        return userFacade.checkBalance(uuid = checkBalanceRequest.uuid).let { user ->
            ApiResponse.ok(UserBalanceResponse(userName = user.name, balance = user.balance))
        }
    }
}