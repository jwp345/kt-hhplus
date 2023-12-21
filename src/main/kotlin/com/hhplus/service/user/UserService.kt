package com.hhplus.service.user

import com.hhplus.component.ConcertInfo
import com.hhplus.component.ReserveMapGetter
import com.hhplus.component.TicketInfo
import com.hhplus.component.UserReader
import com.hhplus.controller.ApiResponse
import com.hhplus.controller.user.PaymentResponse
import com.hhplus.controller.user.UserBalanceDto
import com.hhplus.exception.InvalidTicketException
import com.hhplus.exception.NotEnoughMoneyException
import com.hhplus.model.User
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService (val userReader: UserReader, val reserveMapGetter: ReserveMapGetter) {
    fun chargeMoney(userId: Long, amount: Int, uuid : String) : ApiResponse<UserBalanceDto> {
        val user : User = userReader.validCheckAndRead(userId = userId, uuid = uuid)
        user.balance += amount
        return ApiResponse.ok(UserBalanceDto(userName = user.name, balance = user.balance))
    }

    fun checkBalance(userId: Long, uuid: String) : ApiResponse<UserBalanceDto> {
        val user : User = userReader.validCheckAndRead(userId = userId, uuid = uuid)
        return ApiResponse.ok(UserBalanceDto(userName = user.name, balance = user.balance))
    }

    fun payMoney(userId: Long, seatId: Int, concertDate: String, uuid : String): ApiResponse<PaymentResponse> {
        val user : User = userReader.validCheckAndRead(userId = userId, uuid = uuid)
        val reserveMap = reserveMapGetter.getLockAndReserveMap().mapCache
        val ticket : TicketInfo = reserveMap[ConcertInfo(seatId, concertDate)] ?: throw InvalidTicketException()
        if(user.balance < ticket.price) {
            throw NotEnoughMoneyException()
        }
        user.balance -= ticket.price
        return ApiResponse.ok(PaymentResponse(userId = userId, seatId = seatId, concertDate = concertDate,
            cost = ticket.price, balance = user.balance, paymentDate = LocalDateTime.now())
        )
    }

}