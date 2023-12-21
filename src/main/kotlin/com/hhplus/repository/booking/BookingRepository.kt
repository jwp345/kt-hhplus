package com.hhplus.repository.booking

import com.hhplus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookingRepository : JpaRepository<Booking, Long>{
    @Query("SELECT b.concertDate FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode")
    fun findDatesBySeatId(seatId: Int, availableCode: Int) : List<String>

    @Query("SELECT b.seatId FROM Booking b WHERE b.concertDate = :concertDate AND b.status = :availableCode")
    fun findSeatIdByConcertDate(concertDate: String, availableCode: Int) : List<Int>

    @Query("SELECT b FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode" +
            " AND b.concertDate = :concertDate")
    fun findIdAndPricesBySeatIdAndConcertDateAndStatus(seatId: Int, concertDate: String, availableCode: Int) : List<Booking>
}