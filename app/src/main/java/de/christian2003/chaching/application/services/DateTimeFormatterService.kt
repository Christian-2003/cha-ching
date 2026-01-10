package de.christian2003.chaching.application.services

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class DateTimeFormatterService {

    fun format(date: LocalDate): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        return date.format(formatter)
    }


    fun format(time: LocalDateTime): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        return time.format(formatter)
    }

}
