package com.hhplus.presentation.payment

import com.hhplus.domain.info.ConcertInfo


data class PaymentCommand(val concertInfos : List<ConcertInfo>, val uuid : Long) {
}