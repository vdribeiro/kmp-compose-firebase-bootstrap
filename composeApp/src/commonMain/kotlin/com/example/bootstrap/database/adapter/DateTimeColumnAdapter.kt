package com.example.bootstrap.database.adapter

import app.cash.sqldelight.ColumnAdapter
import com.example.bootstrap.datetime.DateTime

internal object DateTimeColumnAdapter: ColumnAdapter<DateTime, String> {
    override fun decode(databaseValue: String): DateTime = DateTime(databaseValue)
    override fun encode(value: DateTime): String = value.toString()
}
