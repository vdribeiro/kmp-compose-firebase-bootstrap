package com.example.bootstrap.datetime

import com.example.bootstrap.logger.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * UTC representation in ISO8601.
 */
data class DateTime(val instant: Instant) {

    constructor(): this(instant = Clock.System.now())

    constructor(iso8601: String): this(instant = Instant.parse(iso8601))

    constructor(epoch: Long): this(instant = Instant.fromEpochMilliseconds(epoch))

    /**
     * The [iso8601] property in the [UTC_DATE_TIME] format.
     */
    private val iso8601: String by lazy { instant.toString().replace(regex = "\\.\\d+".toRegex(), replacement = "") }

    /**
     * Get the gregorian representation in the device timezone.
     */
    fun toGregorianDateTime(): GregorianDateTime = GregorianDateTime(dateTime = this)

    /**
     * Get the number of milliseconds from the epoch instant 1970-01-01T00:00:00Z.
     * Any fractional part of millisecond is rounded down to the whole number of milliseconds.
     */
    fun toEpoch() = instant.toEpochMilliseconds()

    operator fun plus(period: Period): DateTime =
        DateTime(instant = instant.plus(value = period.milliseconds, unit = DateTimeUnit.MILLISECOND))

    operator fun minus(period: Period): DateTime =
        DateTime(instant = instant.minus(value = period.milliseconds, unit = DateTimeUnit.MILLISECOND))

    operator fun minus(dateTime: DateTime): Period =
        Period(duration = instant.minus(other = dateTime.instant))

    fun toDateString(): String = iso8601.substringBefore(delimiter = "T")

    fun toTimeString(): String = iso8601.substringAfterLast(delimiter = "T")

    override fun toString(): String = iso8601

    override fun equals(other: Any?): Boolean =
        (this === other) || (other is DateTime && iso8601 == other.iso8601)

    override fun hashCode(): Int = iso8601.hashCode()
}

internal const val UTC_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'"

internal fun String.toDateTime(dateOnly: Boolean = false): DateTime? = try {
    if (dateOnly) DateTime(iso8601 = "${this}T00:00:00Z") else DateTime(iso8601 = this)
} catch (throwable: Throwable) {
    Logger.error(tag = "DateTime", message = "Invalid ISO8601 $this.\n${throwable.printStackTrace()}")
    null
}

internal fun Long.toDateTime(): DateTime? = try {
    DateTime(this)
} catch (throwable: Throwable) {
    Logger.error(tag = "DateTime", message = "Invalid Epoch $this.\n${throwable.printStackTrace()}")
    null
}
