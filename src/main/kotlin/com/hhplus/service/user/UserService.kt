package com.hhplus.service.user

import com.hhplus.component.UserReader
import com.hhplus.controller.ApiResponse
import com.hhplus.controller.user.UserBalanceDto
import com.hhplus.model.user.User
import org.springframework.stereotype.Service

@Service
class UserService (val userReader: UserReader) {
    fun chargeMoney(userId: Long, amount: Int, uuid : String) : ApiResponse<UserBalanceDto> {
        val user : User = userReader.read(userId = userId, uuid = uuid)
        user.balance += amount
        return ApiResponse.ok(UserBalanceDto(userName = user.name, balance = user.balance))
    }

    fun checkBalance(userId: Long, uuid: String) : ApiResponse<UserBalanceDto> {
        val user : User = userReader.read(userId = userId, uuid = uuid)
        return ApiResponse.ok(UserBalanceDto(userName = user.name, balance = user.balance))
    }

}