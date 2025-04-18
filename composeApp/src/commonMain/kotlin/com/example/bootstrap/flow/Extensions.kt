package com.example.bootstrap.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal fun CoroutineDispatcher.launch(block: suspend CoroutineScope.() -> Unit): Job =
    CoroutineScope(this).launch(this) { block() }

internal fun <Q> CoroutineDispatcher.async(block: suspend CoroutineScope.() -> Q): Deferred<Q> =
    CoroutineScope(this).async(this) { block() }
