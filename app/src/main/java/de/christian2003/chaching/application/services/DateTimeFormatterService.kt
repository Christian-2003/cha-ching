package de.christian2003.chaching.application.services

import com.ibm.icu.text.RelativeDateTimeFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.min


class DateTimeFormatterService {

    fun format(date: LocalDate): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        return date.format(formatter)
    }


    fun format(time: LocalDateTime): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        return time.format(formatter)
    }


    fun formatRelative(time: LocalDateTime): String {
        val formatter: RelativeDateTimeFormatter = RelativeDateTimeFormatter.getInstance()
        val duration: Duration = Duration.between(time, LocalDateTime.now())

        val seconds: Long = duration.seconds
        val future: Boolean = seconds < 0

        //Disregard future times
        if (future) {
            return format(time)
        }

        if (seconds < 60) {
            return formatter.format(-seconds.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.SECOND)
        }

        val minutes: Long = seconds / 60
        if (minutes < 60) {
            return formatter.format(-minutes.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.MINUTE)
        }

        val hours: Long = minutes / 60
        if (hours < 24) {
            return formatter.format(-hours.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.HOUR)
        }

        val days: Long = hours / 24
        if (days < 7) {
            return formatter.format(-days.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.DAY)
        }

        val week: Long = days / 7
        if (week < 4) {
            return formatter.format(-week.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.WEEK)
        }

        val month: Long = days / 30
        if (month < 12) {
            return formatter.format(-month.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.MONTH)
        }

        val year: Long = days / 365
        return formatter.format(-year.toDouble(), RelativeDateTimeFormatter.RelativeDateTimeUnit.YEAR)
    }

}
