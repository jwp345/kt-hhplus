//package com.hhplus.component
//
//import com.hhplus.domain.repository.BookingRepository
//import com.hhplus.domain.repository.TicketRepository
//import com.hhplus.domain.repository.ValidWaitTokenRepository
//import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//
//@SpringBootTest
//internal class PaymentProcessorTest {
//    @Autowired
//    private lateinit var ticketRepository : TicketRepository
//    @Autowired
//    private lateinit var paymentRepository : PaymentRepositoryImpl
//    @Autowired
//    private lateinit var userReader : UserReader
//    @Autowired
//    private lateinit var waitTokenRepository: ValidWaitTokenRepository : WaitOrderRepository
//    @Autowired
//    private lateinit var bookingRepository : BookingRepository
//
//    private val paymentProcessor = PaymentProcessor(
//            ticketRepository = ticketRepository,
//            paymentRepository = paymentRepository,
//            userReader = userReader,
//            waitOrderRepository = waitOrderRepository,
//            bookingRepository = bookingRepository
//        )
//
//    @Test
//    fun `결제가 완료되면 예약 테이블의 변경 상태가 기록 되어야하며 이력이 저장되어야 한다`() {
//
//    }
//
//}