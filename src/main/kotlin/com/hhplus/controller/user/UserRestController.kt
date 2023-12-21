package com.hhplus.controller.user

import com.hhplus.controller.ApiResponse
import com.hhplus.service.user.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserRestController(val userService: UserService) {

    /* TODO : 시큐리티 구성 후 접속 정보(uuid)와 요청하는 userId 값이 같은지 확인 */
    @PostMapping("/{userId}/charge")
    fun chargeMoney(@PathVariable userId : Long, @RequestParam("amount") amount : Int) : ApiResponse<UserBalanceDto> {
        return userService.chargeMoney(userId = userId, amount = amount, uuid = "uuid")
    }

    @GetMapping("/{userId}/check/balance")
    fun checkBalance(@PathVariable userId: Long) : ApiResponse<UserBalanceDto>{
        return userService.checkBalance(userId = userId, uuid = "uuid")
    }

    @PostMapping("/payment")
    fun payMoney(@RequestBody request : PaymentRequest) : ApiResponse<PaymentResponse> {
        return userService.payMoney(userId = request.userId, seatId = request.seatId, concertDate = request.concertDate, uuid = "uuid")
    }
}