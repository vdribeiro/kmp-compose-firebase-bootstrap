package com.example.bootstrap.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.bootstrap.appContext
import database.AppDatabase

internal actual fun getDriver(): SqlDriver =
    AndroidSqliteDriver(
        context = appContext,
        schema = AppDatabase.Schema,
        name = DatabaseFactory.NAME
    )
