package com.example.bootstrap.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal fun <T: Any, R> Query<T>.getFlow(transform: suspend (value: List<T>) -> R): Flow<R> =
    asFlow().distinctUntilChanged().map { transform(it.executeAsList()) }

/**
 * Sanitize and escape [this] string with the given [escaper].
 */
internal fun String.sanitize(escaper: String = "'"): String =
    "$escaper${replace("'", "''")}${escaper.reversed()}"

/**
 * Convert [this] string into a SQL searchable term, i.e. normalize it to ASCII characters and escape it.
 */
internal fun String?.toSearchTerm(): String =
    orEmpty().normalizeToAscii().sanitize("%")
