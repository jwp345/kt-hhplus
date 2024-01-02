package com.hhplus.presentation.payment


data class PaymentCommand(val concertInfos : List<ConcertInfo>, val uuid : Long) {
}