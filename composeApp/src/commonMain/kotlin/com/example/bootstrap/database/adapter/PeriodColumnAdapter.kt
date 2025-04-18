package com.example.bootstrap.database.adapter

import app.cash.sqldelight.ColumnAdapter
import com.example.bootstrap.datetime.Period

internal object PeriodColumnAdapter: ColumnAdapter<Period, String> {
    override fun decode(databaseValue: String): Period = Period(databaseValue)
    override fun encode(value: Period): String = value.toString()
}
