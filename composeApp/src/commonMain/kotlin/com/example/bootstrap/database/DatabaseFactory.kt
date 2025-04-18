package com.example.bootstrap.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import com.example.bootstrap.database.adapter.DateTimeColumnAdapter
import database.AppDatabase
import database.ScheduledJob
import database.User

internal object DatabaseFactory {

    private val scheduledJobAdapter = ScheduledJob.Adapter(
        utcAdapter = DateTimeColumnAdapter,
        actionAdapter = IntColumnAdapter,
        stateAdapter = EnumColumnAdapter(),
        attemptAdapter = IntColumnAdapter
    )

    private val userAdapter = User.Adapter(
        typeAdapter = EnumColumnAdapter(),
        modifiedAtAdapter = DateTimeColumnAdapter,
        lastSignInAtAdapter = DateTimeColumnAdapter
    )

    const val NAME = "Database.db"

    val driver = getDriver()

    fun getDatabase(): AppDatabase = AppDatabase(
        driver = driver,
        ScheduledJobAdapter = scheduledJobAdapter,
        UserAdapter = userAdapter
    )
}
