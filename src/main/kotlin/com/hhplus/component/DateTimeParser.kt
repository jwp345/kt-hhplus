package com.hhplus.component

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTimeParser {

    fun dateTimeToString(dateTime : LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }

    fun stringToDateTime(strTime : String) : LocalDateTime {
        return LocalDateTime.parse(strTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}