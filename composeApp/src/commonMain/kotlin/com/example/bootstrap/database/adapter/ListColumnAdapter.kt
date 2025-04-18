package com.example.bootstrap.database.adapter

import app.cash.sqldelight.ColumnAdapter
import com.example.bootstrap.http.decode
import com.example.bootstrap.http.encode

internal object ListColumnAdapter: ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> = databaseValue.decode()
    override fun encode(value: List<String>): String = value.encode()
}
