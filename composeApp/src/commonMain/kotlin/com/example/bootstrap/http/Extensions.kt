package com.example.bootstrap.http

import com.example.bootstrap.datetime.DateTime
import com.example.bootstrap.datetime.toDateTime

private const val separator = ";,:."

internal fun <E> Collection<E>.encode(): String =
    joinToString(separator = separator)

internal fun String.decode(): List<String> =
    if (isEmpty()) listOf() else split(separator)

internal fun String.toBool(): Boolean =
    with(lowercase()) { toBooleanStrictOrNull() ?: (this == "1") }

internal fun Boolean?.toBoolString(): String =
    this?.let { if (it) 1 else 0 }.toString()

internal fun Map<String, Any?>.getString(key: String): String? =
    get(key = key)?.toString()

internal fun Map<String, Any?>.getFloat(key: String): Float? =
    getString(key = key)?.toFloatOrNull()

internal fun Map<String, Any?>.getBoolean(key: String): Boolean? =
    getString(key = key)?.toBool()

internal fun Map<String, Any?>.getDateTime(key: String): DateTime? =
    getString(key = key)?.toDateTime()

@Suppress("UNCHECKED_CAST")
internal fun <T> Map<String, Any?>.getList(key: String): List<T> =
    when (val value = get(key)) {
        is Collection<*> -> value
        else -> value?.toString()?.decode()
    } as? List<T> ?: emptyList()

internal fun Map<String, Any?>.toStringMap(): Map<String, String> =
    buildMap {
        this@toStringMap.forEach { entry ->
            put(
                key = entry.key,
                value = when (val value = entry.value ?: return@forEach) {
                    is Collection<*> -> value.encode()
                    else -> entry.value.toString()
                }
            )
        }
    }
