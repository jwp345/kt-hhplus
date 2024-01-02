package com.hhplus.presentation.payment

import com.hhplus.application.PaymentFacade
import com.hhplus.component.UserValidator
import com.hhplus.infrastructure.security.CustomUser
import com.hhplus.presentation.ApiResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentRestController(val paymentFacade: PaymentFacade, val userValidator: UserValidator) {

    @PostMapping("")
    fun payMoney(@RequestBody command : PaymentCommand) : ApiResponse<List<PaymentResponse>> {
        userValidator.checkTokenAndUser(userUuid = command.uuid)
        return paymentFacade.payMoney(concertInfos = command.concertInfos,
            waitToken = (SecurityContextHolder.getContext().authentication.principal as CustomUser).token)
            .let{ payments ->
                ApiResponse.ok(
                    payments.stream().map { payment ->
                        PaymentResponse(
                        uuid = payment.uuid,
                        seatId = payment.seatId,
                        bookingDate = payment.bookingDate,
                        cost = payment.price,
                        paymentDate = payment.paymentDate
                    ) }.toList()
                )
            }
    }
}