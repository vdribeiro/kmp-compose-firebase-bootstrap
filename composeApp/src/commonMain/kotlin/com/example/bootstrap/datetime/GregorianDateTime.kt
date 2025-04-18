package com.example.bootstrap.datetime

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Gregorian representation for a date time in a specific time zone.
 */
data class GregorianDateTime(
    private val localDateTime: LocalDateTime,
    private val timeZone: TimeZone
) {
    val utc: DateTime by lazy { DateTime(localDateTime.toInstant(timeZone)) }

    constructor(
        year: Int = 0,
        month: Int = 1,
        dayOfMonth: Int = 1,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): this(
        localDateTime = LocalDateTime(
            year = year,
            monthNumber = month,
            dayOfMonth = dayOfMonth,
            hour = hour,
            minute = minute,
            second = second,
            nanosecond = 0
        ),
        timeZone = timeZone
    )

    constructor(
        dateTime: DateTime = DateTime(),
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): this(
        timeZone = timeZone,
        localDateTime = dateTime.instant.toLocalDateTime(timeZone)
    )

    val timeZoneId get() = timeZone.id

    val year: Int get() = localDateTime.year
    /**
     * Year in the "yyyy" format.
     */
    val yearString: String get() = getYearString(year)

    val month: Int get() = localDateTime.monthNumber
    /**
     * Month in the "MM" format.
     */
    val monthString: String get() = getMonthString(month)
    val monthName: String get() = localDateTime.month.name
    val monthNames: List<String> = Month.values().map { it.name }

    val day: Int get() = localDateTime.dayOfMonth
    /**
     * Day in the "dd" format.
     */
    val dayString: String get() = getDayString(day)
    /**
     * Day of the year from 1 to 365 or 366 for leap years.
     */
    val dayOfTheYear: Int get() = localDateTime.dayOfYear
    /**
     * Day of the month from 1 to 28, 29, 30 or 31 depending on the month and leap year.
     */
    val dayOfTheMonth: Int get() = localDateTime.dayOfMonth
    /**
     * Number of the day of the week. Monday is 1, Sunday is 7.
     */
    val dayOfTheWeek: Int get() = localDateTime.dayOfWeek.isoDayNumber
    val dayOfTheWeekName: String get() = localDateTime.dayOfWeek.name
    val weekDayNames: List<String> = DayOfWeek.values().map { it.name }

    val hour: Int get() = localDateTime.hour
    /**
     * Hour in the "HH" format.
     */
    val hourString: String get() = getHourString(hour)

    val minute: Int get() = localDateTime.minute
    /**
     * Minute in the "mm" format.
     */
    val minuteString: String get() = getMinuteString(minute)

    val second: Int get() = localDateTime.second
    /**
     * Second in the "ss" format.
     */
    val secondString: String get() = getSecondString(second)

    operator fun plus(period: Period): GregorianDateTime =
        (utc + period).toGregorianDateTime()

    operator fun minus(period: Period): GregorianDateTime =
        (utc - period).toGregorianDateTime()

    operator fun minus(gregorian: GregorianDateTime): Period =
        utc - gregorian.utc

    override fun toString(): String = "$yearString-$monthString-$dayString $hourString:$minuteString:$secondString $timeZoneId"

    override fun equals(other: Any?): Boolean =
        (this === other) || (other is GregorianDateTime && utc == other.utc && timeZone.id == other.timeZone.id)

    override fun hashCode(): Int {
        var result = utc.hashCode()
        result = 31 * result + timeZone.id.hashCode()
        return result
    }
}
