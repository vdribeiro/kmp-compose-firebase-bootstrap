package com.example.bootstrap.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.JournalMode
import co.touchlab.stately.concurrency.Lock
import database.AppDatabase

internal actual fun getDriver(): SqlDriver =
    ListenerWorkaroundNativeSqliteDriver(
        schema = AppDatabase.Schema,
        name = DatabaseFactory.name,
        onConfiguration = { config ->
            config.copy(
                journalMode = JournalMode.DELETE
            )
        },
        maxReaderConnections = 6
    )

// Workaround based on https://github.com/cashapp/sqldelight/pull/4567,
// that addresses https://github.com/cashapp/sqldelight/issues/4376
private class ListenerWorkaroundNativeSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
    onConfiguration: (DatabaseConfiguration) -> DatabaseConfiguration = { it },
    maxReaderConnections: Int = 1,
): SqlDriver by NativeSqliteDriver(
    schema = schema,
    name = name,
    onConfiguration = onConfiguration,
    maxReaderConnections = maxReaderConnections
) {
    private val listeners = mutableMapOf<String, MutableSet<Query.Listener>>()
    private val lock = Lock()
    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        lock.lock()
        queryKeys.forEach {
            listeners.getOrPut(key = it) { mutableSetOf() }.add(element = listener)
        }
        lock.unlock()
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        lock.lock()
        queryKeys.forEach {
            listeners[it]?.remove(element = listener)
        }
        lock.unlock()
    }

    @Suppress("UselessCallOnCollection")
    override fun notifyListeners(vararg queryKeys: String) {
        lock.lock()
        queryKeys.flatMap { listeners[it] ?: emptySet() }
            .asSequence()
            // This shouldn't be necessary, but adding an extra guard to avoid
            // https://github.com/cashapp/sqldelight/issues/4376
            .filterNotNull()
            .forEach(action = Query.Listener::queryResultsChanged)
        lock.unlock()
    }
}