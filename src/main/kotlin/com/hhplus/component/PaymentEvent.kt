package com.hhplus.component

import com.hhplus.domain.entity.Payment

data class PaymentEvent(val payments : MutableList<Payment>)
