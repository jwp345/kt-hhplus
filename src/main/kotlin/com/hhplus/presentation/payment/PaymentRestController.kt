package com.hhplus.presentation.payment

import com.hhplus.application.PaymentFacade
import com.hhplus.presentation.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentRestController(val paymentFacade: PaymentFacade) {

    @PostMapping("")
    suspend fun payMoney(@RequestBody command : PaymentCommand) : ApiResponse<PaymentResponse> {
        return paymentFacade.payMoney(uuid = command.uuid, seatId = command.seatId, bookingDate = command.bookingDate)
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