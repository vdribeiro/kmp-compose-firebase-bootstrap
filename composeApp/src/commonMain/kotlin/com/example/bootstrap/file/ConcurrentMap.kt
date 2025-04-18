package com.example.bootstrap.file

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal open class ConcurrentMap<Key, Value>: MutableMap<Key, Value> by HashMap() {
    private val mutex = Mutex()

    suspend fun synchronizedPut(key: Key, value: Value): Value? =
        mutex.withLock { put(key = key, value = value) }

    suspend fun synchronizedGet(key: Key): Value? =
        mutex.withLock { get(key = key) }
}
