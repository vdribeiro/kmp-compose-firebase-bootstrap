package com.example.bootstrap.datetime

/**
 * Get year with 4 digits.
 */
internal fun getYearString(year: Int): String = with(year) {
    when {
        this < 0 -> "0000"
        this > 9999 -> "9999"
        this in 0..9 -> "000$this"
        this in 10..99 -> "00$this"
        this in 100..999 -> "0$this"
        else -> this.toString()
    }
}

/**
 * Get month with 2 digits.
 */
internal fun getMonthString(month: Int): String = with(month) {
    when {
        this < 1 -> "01"
        this > 12 -> "12"
        this in 1..9 -> "0$this"
        else -> this.toString()
    }
}

/**
 * Get day with 2 digits.
 */
internal fun getDayString(day: Int): String = with(day) {
    when {
        this < 1 -> "01"
        this > 31 -> "31"
        this in 1..9 -> "0$this"
        else -> this.toString()
    }
}

/**
 * Get hour with 2 digits.
 */
internal fun getHourString(hour: Int): String = with(hour) {
    when {
        this < 0 -> "00"
        this > 24 -> "24"
        this in 0..9 -> "0$this"
        else -> this.toString()
    }
}

/**
 * Get minute with 2 digits.
 */
internal fun getMinuteString(minute: Int): String = with(minute) {
    when {
        this < 0 -> "00"
        this > 59 -> "59"
        this in 0..9 -> "0$this"
        else -> this.toString()
    }
}

/**
 * Get second with 2 digits.
 */
internal fun getSecondString(second: Int): String = with(second) {
    when {
        this < 0 -> "00"
        this > 59 -> "59"
        this in 0..9 -> "0$this"
        else -> this.toString()
    }
}
