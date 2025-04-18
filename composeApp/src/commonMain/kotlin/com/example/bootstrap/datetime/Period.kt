package com.example.bootstrap.datetime

import com.example.bootstrap.datetime.Period.Pattern.PERIOD
import com.example.bootstrap.datetime.Period.Pattern.TIME
import com.example.bootstrap.logger.Logger
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Period representation in ISO8601.
 *
 * Available formats are [PERIOD] 'PdDThHmMs.fS' and [TIME] 'd:h:m:s.fff' where 'd', 'h', 'm', 's' and 'f' are the
 * days, hours, minutes, seconds and fractional part of second of the duration respectively. Each value is optional, meaning that
 * the [TIME] format is parsed from the lowest available and the fractional part must be of length 3 if it exists, and
 * the [PERIOD] format always starts with a 'P' character and optionally has each temporal type has a single-letter suffix that indicates the unit.
 * There must be at least one unit specified and the letter 'T' must prefix clock units. Examples:
 * - [PERIOD] as P1DT2H3M4.005S and [TIME] as '01:02:03:04.005' is 1 day, 2 hours, 3 minutes, 4 seconds and 5 milliseconds.
 * - [PERIOD] as PT1M20S and [TIME] as '1:20' is 1 minutes and 20 seconds.
 * - [PERIOD] as PT3H2M1S and [TIME] as '3:2:1' is 3 hours, 2 minutes and 1 second.
 * - [PERIOD] as PT54321S and [TIME] as '54321' is 54321 seconds.
 * - [PERIOD] as P40D and [TIME] as '40:00:00:00' is forty days.
 * - [PERIOD] as P3DT4H59M and [TIME] as '03:4:59:0' is three days, four hours and 59 minutes.
 * - [PERIOD] as PT2H30M and [TIME] as '2:30:0' is two and a half hours.
 * - [PERIOD] as PT0.002S and [TIME] as '0.002' is 2 milliseconds.
 * - [PERIOD] as P or PT returns an error as there must be at least one unit specified.
 * - [TIME] as '1.1' returns an error as the milliseconds must have 3 digits.
 */
class Period {

    private val duration: Duration

    /**
     * Period from a [durationString] in the [inputPattern] format.
     */
    constructor(durationString: String, inputPattern: Pattern) {
        try {
            this.duration = when (inputPattern) {
                PERIOD -> Duration.parse(durationString)
                TIME -> getDurationFromTime(durationString)
            }
        } catch (throwable: Throwable) {
            Logger.error(tag = TAG, message = "Invalid arguments: $durationString <-> $inputPattern")
            throw throwable
        }
    }

    /**
     * The [iso8601] property must comply to the [PERIOD] format.
     */
    constructor(iso8601: String): this(iso8601, PERIOD)

    internal constructor(duration: Duration) {
        this.duration = duration
    }

    val days: Long
        get() = duration.inWholeDays
    val hours: Long
        get() = duration.inWholeHours
    val minutes: Long
        get() = duration.inWholeMinutes
    val seconds: Long
        get() = duration.inWholeSeconds
    val milliseconds: Long
        get() = duration.inWholeMilliseconds

    override fun toString(): String = duration.toIsoString()

    @Throws(Throwable::class)
    private fun getDurationFromTime(durationString: String): Duration {
        val split = durationString.split(":").reversed()
        val fractional = split[0].split(".")
        val fractionalMillis = fractional.getOrNull(index = 1)
        if (fractionalMillis != null && fractionalMillis.length != 3) {
            throw IllegalArgumentException("Invalid duration $durationString")
        }

        val get: (List<String>, Int, DurationUnit) -> Duration = { list, index, unit ->
            (list.getOrNull(index)?.toInt() ?: 0).toDuration(unit)
        }
        val milliseconds = get(fractional, 1, DurationUnit.MILLISECONDS)
        val seconds = get(fractional, 0, DurationUnit.SECONDS)
        val minutes = get(split, 1, DurationUnit.MINUTES)
        val hours = get(split, 2, DurationUnit.HOURS)
        val days = get(split, 3, DurationUnit.DAYS)
        return days + hours + minutes + seconds + milliseconds
    }

    operator fun plus(period: Period): Period =
        Period(duration = this.duration + period.duration)

    operator fun minus(period: Period): Period =
        Period(duration = this.duration - period.duration)

    override fun equals(other: Any?): Boolean =
        (this === other) || (other is Period && duration == other.duration)

    override fun hashCode(): Int = duration.hashCode()

    enum class Pattern {
        PERIOD,
        TIME
    }

    companion object {
        internal const val TAG = "Period"
    }
}

internal fun String.toPeriod(inputPattern: Period.Pattern = TIME): Period? = try {
    Period(this, inputPattern)
} catch (throwable: Throwable) {
    Logger.error(tag = Period.TAG, message = "Invalid period $this with pattern $inputPattern\n${throwable.printStackTrace()}")
    null
}
