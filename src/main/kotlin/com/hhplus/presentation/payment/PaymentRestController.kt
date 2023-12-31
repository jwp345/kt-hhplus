package com.hhplus.presentation.payment

import com.hhplus.application.PaymentFacade
import com.hhplus.component.UserValidator
import com.hhplus.infrastructure.security.CustomUser
import com.hhplus.presentation.ApiResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentRestController(val paymentFacade: PaymentFacade, val userValidator: UserValidator) {

    @PostMapping("")
    fun payMoney(@RequestBody command : PaymentCommand) : ApiResponse<PaymentResponse> {
        userValidator.checkTokenAndUser(userUuid = command.uuid) // -> 어노테이션으로 빼는 거 고민
        return paymentFacade.payMoney(uuid = command.uuid, seatId = command.seatId,
            bookingDate = command.bookingDate, waitToken = (SecurityContextHolder.getContext().authentication.principal as CustomUser).token)
            .let{ payment ->
                ApiResponse.ok(
                    PaymentResponse(
                        uuid = payment.uuid,
                        seatId = payment.seatId,
                        bookingDate = payment.bookingDate,
                        cost = payment.price,
                        paymentDate = payment.paymentDate
                    )
                )
            }
    }
}